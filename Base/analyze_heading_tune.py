#!/usr/bin/env python3
"""
Heading PID Tuning Analyzer for XRP Robot
==========================================
Usage:
  python analyze_heading_tune.py           # analyzes the most recent log in logs/
  python analyze_heading_tune.py <file>    # analyzes a specific CSV file

After a drive session:
  1. Enable the robot and wait for calibration (3 seconds).
  2. Drive forward for at least 2-3 seconds without touching the turn stick
     so the heading lock engages.
  3. Gently push the robot sideways by hand (or let it drift naturally).
  4. Keep driving forward and watch whether it corrects back.
  5. Disable the robot.
  6. Run this script.
"""

import csv
import os
import sys
import glob
import math
import re


# ---------------------------------------------------------------------------
# FILE LOADING
# ---------------------------------------------------------------------------

def find_latest_log():
    log_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "logs")
    files = glob.glob(os.path.join(log_dir, "heading_tune_*.csv"))
    if not files:
        return None
    return max(files, key=os.path.getmtime)


def load_csv(filename):
    rows = []
    with open(filename, newline="") as f:
        reader = csv.DictReader(f)
        for row in reader:
            try:
                rows.append({
                    "time":              float(row["timestamp_s"]),
                    "heading":           float(row["heading_deg"]),
                    "locked":            float(row["locked_heading_deg"]),
                    "error":             float(row["heading_error_deg"]),
                    "yaw_rate":          float(row["yaw_rate_degs"]),
                    "raw_correction":    float(row["raw_correction"]),
                    "applied":           float(row["applied_correction"]),
                    "forward":           float(row["forward_input"]),
                    "locked_state":      row["heading_locked"].strip().lower() == "true",
                })
            except (ValueError, KeyError):
                pass  # skip malformed rows
    return rows


# ---------------------------------------------------------------------------
# SEGMENT EXTRACTION
# Find continuous periods where heading is locked AND robot is moving forward.
# A gap > 0.5 s between active rows starts a new segment.
# Only keep segments longer than min_duration.
# ---------------------------------------------------------------------------

def find_active_segments(data, min_duration=1.5, max_gap=0.5):
    segments = []
    current = []

    for row in data:
        active = row["locked_state"] and row["forward"] > 0.05
        if active:
            if current:
                gap = row["time"] - current[-1]["time"]
                if gap > max_gap:
                    # save the segment we were building and start a new one
                    if current[-1]["time"] - current[0]["time"] >= min_duration:
                        segments.append(current)
                    current = []
            current.append(row)
        else:
            if current and current[-1]["time"] - current[0]["time"] >= min_duration:
                segments.append(current)
            current = []

    if current and current[-1]["time"] - current[0]["time"] >= min_duration:
        segments.append(current)

    return segments


# ---------------------------------------------------------------------------
# SEGMENT ANALYSIS
# ---------------------------------------------------------------------------

def count_zero_crossings(errors):
    """Number of times the error signal crosses zero (each crossing = half oscillation)."""
    crossings = 0
    for i in range(1, len(errors)):
        if errors[i - 1] * errors[i] < 0:
            crossings += 1
    return crossings


def settling_time(segment, threshold_deg=1.5, hold_seconds=0.4):
    """
    Time from segment start until |error| stays below threshold_deg for hold_seconds.
    Returns None if it never settles.
    """
    hold_samples = max(1, int(hold_seconds / 0.020))
    errors = [abs(r["error"]) for r in segment]
    t0 = segment[0]["time"]

    for i in range(len(errors) - hold_samples):
        if all(e < threshold_deg for e in errors[i : i + hold_samples]):
            return segment[i]["time"] - t0

    return None


def saturation_fraction(segment, max_correction):
    """Fraction of samples where |raw_correction| >= 95% of the clamp limit."""
    saturated = sum(
        1 for r in segment if abs(r["raw_correction"]) >= 0.95 * max_correction
    )
    return saturated / len(segment)


def analyze_segment(seg, max_correction):
    errors = [r["error"] for r in seg]
    abs_errors = [abs(e) for e in errors]
    duration = seg[-1]["time"] - seg[0]["time"]

    return {
        "duration":        duration,
        "max_error":       max(abs_errors),
        "mean_error":      sum(abs_errors) / len(abs_errors),
        "zero_crossings":  count_zero_crossings(errors),
        "settling_time":   settling_time(seg),
        "sat_fraction":    saturation_fraction(seg, max_correction),
    }


# ---------------------------------------------------------------------------
# READ KP AND MAX_CORRECTION FROM Constants.java
# ---------------------------------------------------------------------------

def read_constants():
    constants_path = os.path.join(
        os.path.dirname(os.path.abspath(__file__)),
        "src", "main", "java", "frc", "robot", "Constants.java",
    )
    kp = None
    max_correction = None
    try:
        with open(constants_path) as f:
            for line in f:
                # Match lines like:   public static final double HEADING_KP = 10;
                m = re.search(r"HEADING_KP\s*=\s*([\d.]+)", line)
                if m and "//" not in line.split("HEADING_KP")[0]:
                    kp = float(m.group(1))
                m = re.search(r"HEADING_MAX_CORRECTION\s*=\s*([\d.]+)", line)
                if m and "//" not in line.split("HEADING_MAX_CORRECTION")[0]:
                    max_correction = float(m.group(1))
    except FileNotFoundError:
        pass
    return kp or 10.0, max_correction or 1.0


# ---------------------------------------------------------------------------
# RECOMMENDATION ENGINE
# ---------------------------------------------------------------------------

def recommend(analyses, current_kp, max_correction):
    total_crossings = sum(a["zero_crossings"] for a in analyses)
    total_duration  = sum(a["duration"]       for a in analyses)
    settled         = [a["settling_time"] for a in analyses if a["settling_time"] is not None]
    avg_sat         = sum(a["sat_fraction"] for a in analyses) / len(analyses)

    osc_rate        = total_crossings / total_duration if total_duration > 0 else 0
    avg_settle      = sum(settled) / len(settled) if settled else None

    # ---- print summary table ----
    print()
    print("  Segments analyzed  :", len(analyses))
    print(f"  Total drive time   : {total_duration:.1f} s")
    print(f"  Oscillations       : {total_crossings}  ({osc_rate:.2f}/s)")
    print(f"  Clamp saturation   : {avg_sat*100:.0f}% of samples")
    if avg_settle is not None:
        print(f"  Avg settling time  : {avg_settle:.2f} s")
    else:
        print(f"  Settling time      : never settled within 1.5°")
    print(f"  Max error seen     : {max(a['max_error'] for a in analyses):.1f}°")
    print()

    # ---- diagnose and recommend ----
    if avg_sat > 0.60:
        diagnosis = (
            "CLAMP SATURATING — the raw PID output is being cut off more\n"
            "  than 60% of the time. The robot is doing bang-bang control\n"
            "  (full correction on, full correction off) rather than smooth PID.\n"
            "  This causes oscillation that KD cannot fix."
        )
        new_kp = round(current_kp * 0.20, 4)
        advice = (
            f"Set HEADING_KP = {new_kp}  (was {current_kp})\n"
            f"  This brings the raw output into a range the clamp won't cut off."
        )

    elif osc_rate > 1.0:
        diagnosis = (
            f"OSCILLATING — error crosses zero {osc_rate:.1f} times per second.\n"
            "  The gain is too high; the robot overcorrects and overshoots repeatedly."
        )
        # Ziegler-Nichols P-only: KP_optimal ≈ 0.5 × Ku (ultimate gain)
        # Here we treat current KP as Ku since it's already oscillating.
        new_kp = round(current_kp * 0.45, 4)
        advice = (
            f"Set HEADING_KP = {new_kp}  (was {current_kp})\n"
            f"  If it still oscillates, halve again.\n"
            f"  If it settles but slowly, try adding HEADING_KD = {round(current_kp * 0.005, 5)}"
        )

    elif osc_rate > 0.35:
        diagnosis = (
            f"MILDLY OSCILLATING — {osc_rate:.1f} zero-crossings/s.\n"
            "  The response is slightly underdamped; a small reduction will fix it."
        )
        new_kp = round(current_kp * 0.65, 4)
        advice = (
            f"Set HEADING_KP = {new_kp}  (was {current_kp})"
        )

    elif avg_settle is None or avg_settle > 3.0:
        diagnosis = (
            "SLUGGISH — the robot corrects in the right direction but too slowly.\n"
            "  Heading error takes more than 3 seconds to settle (or never does)."
        )
        new_kp = round(current_kp * 2.0, 4)
        advice = (
            f"Set HEADING_KP = {new_kp}  (was {current_kp})\n"
            f"  Double and re-test; keep doubling until you see oscillation,\n"
            f"  then back off 30-40%."
        )

    elif avg_settle > 1.8:
        diagnosis = (
            f"ACCEPTABLE but slow — settling in {avg_settle:.1f} s (target < 1.5 s)."
        )
        new_kp = round(current_kp * 1.4, 4)
        advice = (
            f"Set HEADING_KP = {new_kp}  (was {current_kp})"
        )

    else:
        diagnosis = (
            f"WELL-TUNED — settling in {avg_settle:.1f} s with {osc_rate:.2f} crossings/s.\n"
            "  No KP change needed."
        )
        new_kp = current_kp
        kd_suggestion = round(current_kp * 0.05, 4)
        advice = (
            f"Keep HEADING_KP = {current_kp}\n"
            f"  Optional: if there is still a tiny steady-state drift, try\n"
            f"  HEADING_KI = {round(current_kp * 0.01, 5)}\n"
            f"  Or add HEADING_KD = {kd_suggestion} to sharpen the initial response."
        )

    print(f"  DIAGNOSIS: {diagnosis}")
    print()
    print(f"  RECOMMENDATION: {advice}")
    print()
    if new_kp != current_kp:
        print(
            "  → Open Constants.java and change:\n"
            f"      HEADING_KP = {new_kp};\n"
            "  Then re-enable, drive, disable, and run this script again."
        )


# ---------------------------------------------------------------------------
# MAIN
# ---------------------------------------------------------------------------

def main():
    if len(sys.argv) > 1:
        filename = sys.argv[1]
    else:
        filename = find_latest_log()
        if filename is None:
            print("ERROR: No log files found in logs/")
            print("Drive the robot with heading-hold active, then disable and run this script.")
            sys.exit(1)

    print(f"\nLoading: {os.path.basename(filename)}")
    data = load_csv(filename)
    if not data:
        print("ERROR: Log file is empty or could not be parsed.")
        sys.exit(1)

    span = data[-1]["time"] - data[0]["time"]
    print(f"Rows: {len(data)}   Duration: {span:.1f} s")

    current_kp, max_correction = read_constants()
    print(f"Constants.java → HEADING_KP = {current_kp},  HEADING_MAX_CORRECTION = {max_correction}")

    segments = find_active_segments(data, min_duration=1.5)
    if not segments:
        print(
            "\nNot enough heading-hold driving found (need ≥1.5 s of forward driving\n"
            "with the heading locked).\n\n"
            "Tips:\n"
            "  1. Enable → wait 3 s for calibration\n"
            "  2. Drive forward for 3+ s without touching the turn stick\n"
            "  3. Gently push the robot sideways and keep driving\n"
            "  4. Disable → run this script"
        )
        sys.exit(1)

    print(f"\nFound {len(segments)} active segment(s) for analysis.")

    analyses = [analyze_segment(s, max_correction) for s in segments]

    print("\n" + "=" * 60)
    print("  HEADING PID TUNING REPORT")
    print("=" * 60)
    recommend(analyses, current_kp, max_correction)
    print("=" * 60 + "\n")


if __name__ == "__main__":
    main()
