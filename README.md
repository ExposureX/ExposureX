# ExposureX

**Passive Colorimetric H2S Exposure-Dosimeter Wristband with AI-Based Quantitative Reading**

Smart India Hackathon 2026 | Problem Statement ID: SIH26118 | Theme: Smart Automation | Category: Hardware

Repository: https://github.com/ExposureX/ExposureX

## Overview

ExposureX is a low-cost, disposable wristband paired with an Android app that helps oil and gas refinery workers, especially contract labor, track their cumulative Hydrogen Sulfide (H2S) exposure. Electronic gas detectors are expensive and not issued to every worker. ExposureX closes that gap with a passive colorimetric strip and smartphone-based image analysis instead of costly electronics.

## Problem

- H2S is a highly toxic gas common in oil and gas refineries
- Prolonged low-level exposure often goes undetected
- Contract labor lacks continuous personal monitoring
- Fixed detectors and periodic checks miss cumulative, long-term exposure
- Electronic detectors are expensive and not issued to every worker

## Solution

A disposable wristband whose colorimetric strip darkens progressively and permanently with H2S exposure. The companion app scans the wristband, estimates the cumulative dose, ties the reading to a registered worker, and logs it for supervisor review.

## Features

- **Worker registration**: unique Worker ID (format `WKR-XXXXXXXX`) with a generated QR badge
- **Worker identification**: scan the worker's QR badge to select who is being tested
- **Wristband scanning**: camera capture of the strip (CameraX)
- **Dose estimation**: samples the strip color and matches it against a reference scale (Safe, Low, Moderate, High, Critical)
- **Instant result card**: exposure level, dose percentage, and a gauge
- **Scan log**: every scan (worker ID, timestamp, level, dose) is stored locally on the device (Room database)
- **Supervisor dashboard**: PIN-protected list of all workers with scan counts, latest reading, and a flag for workers who reach the High level; tap a worker to see their full scan log

## Tech Stack

- Kotlin, Android Views (ConstraintLayout, CardView)
- CameraX for photo capture and QR scanning
- ML Kit Barcode Scanning for reading worker QR badges
- ZXing for generating QR badges
- Room (SQLite) with KSP for local storage
- Native Android Bitmap/Color analysis for color matching

## App Workflow

1. Supervisor registers a worker, and the app generates a Worker ID and QR badge
2. Before scanning, the worker's QR badge is scanned to identify them
3. The worker wears the wristband, which reacts passively to H2S with no electronics
4. After the shift, the wristband strip is scanned with the app
5. The app matches the strip color to the reference scale and shows the exposure level
6. The reading is saved against the worker's ID with a timestamp
7. The supervisor opens the dashboard (PIN required) to review all workers and flagged readings

## Getting Started

1. Install Android Studio and clone this repository
2. Open the project and let Gradle sync
3. Connect an Android phone with USB debugging enabled (camera features need a real device)
4. Run the app, register a worker, scan the badge, then scan a wristband strip

Demo supervisor PIN: `1234`

## Project Status and Limitations

This is a working prototype built for the SIH 2026 internal hackathon round.

- The reference color scale is a **placeholder**. Real dose values need lab calibration against known H2S concentrations and exposure durations.
- Lighting correction using the printed reference scale on the wristband is planned but not yet implemented.
- The supervisor PIN is hardcoded for demonstration only. A real deployment needs proper authentication.
- The High-level flag threshold is a placeholder until it is tied to limits from IS 15200 and OISD standards.
- Data is stored locally on the device. Cloud sync and a multi-site dashboard are future work.

## Team

**ExposureX**, Ashoka Women's Engineering College

## References

- Roberts, R.C., et al., "A Liquid Crystal-Based Passive Badge for Personal Monitoring of Exposure to Hydrogen Sulfide," *Journal of Occupational and Environmental Hygiene*
- Bureau of Indian Standards, IS 15200:2002, "Hydrogen Sulphide: Code of Safety"
- Oil Industry Safety Directorate (OISD), Ministry of Petroleum and Natural Gas, Government of India
- OSHA, "Hydrogen Sulfide: Standards," U.S. Department of Labor
