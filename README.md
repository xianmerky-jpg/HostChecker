# Host Checker — Kotlin exact-layout recreation

This version uses a custom Android Canvas layout to keep the supplied Host Checker geometry, colors, spacing, and typography under direct control rather than relying on a generic Material layout.

Behavior is intentionally simulated. Pressing Check always renders the fixed response shown in the supplied second screenshot. It does not perform a network request.

Build through GitHub Actions: push the repository, then open Actions → Build Host Checker APK → completed run → Artifacts → HostChecker-debug.
