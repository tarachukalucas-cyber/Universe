#!/usr/bin/env python3
"""Apply the small, documented Universe branding patch to the pinned GPL-3.0 runtime."""
from pathlib import Path

root = Path(__file__).resolve().parents[1] / "runtime" / "ZalithLauncher2"
gradle = root / "ZalithLauncher" / "build.gradle.kts"
props = root / "ZalithLauncher" / "gradle.properties"
screen = root / "ZalithLauncher" / "src/main/java/com/movtery/zalithlauncher/ui/screens/content/LauncherScreen.kt"

g = gradle.read_text()
upstream_sdk = """compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }"""
if upstream_sdk not in g:
    raise SystemExit("Could not locate upstream compile SDK block; stop instead of patching an unknown build.")
g = g.replace(upstream_sdk, "compileSdk = 36", 1)
old = 'applicationId = zalithPackageName'
new = 'applicationId = "com.universe.launcher"'
if old not in g:
    raise SystemExit("Could not locate upstream applicationId line; stop instead of applying an unsafe patch.")
gradle.write_text(g.replace(old, new, 1))

p = props.read_text()
values = {
    "launcher_name": "Universe",
    "launcher_app_name": "Universe Launcher",
    "launcher_short_name": "UNV",
    "url_home": "https://github.com/tarachukalucas-cyber/Universe",
    "launcher_version_code": "100",
    "launcher_version_name": "1.0.0",
}
lines = p.splitlines()
for key, value in values.items():
    prefix = key + "="
    lines = [line for line in lines if not line.startswith(prefix)]
    lines.append(prefix + value)
props.write_text("\n".join(lines) + "\n")

s = screen.read_text()
anchor = '        BoxWithConstraints(\n'
banner = '''        BoxWithConstraints(
'''
if anchor not in s:
    raise SystemExit("Could not locate launcher home layout; branding patch was not applied.")
# Add a visible notice required by the upstream GPLv3 additional terms for modified builds.
needle = '''        ) {
            val parentWidthPx = constraints.maxWidth.toFloat()
'''
replacement = '''        ) {
            Text(
                text = "UNIVERSE • MODIFICAÇÃO NÃO OFICIAL",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            val parentWidthPx = constraints.maxWidth.toFloat()
'''
if needle not in s:
    raise SystemExit("Could not locate home layout insertion point; stop instead of patching an unknown layout.")
screen.write_text(s.replace(needle, replacement, 1))
print("Universe branding applied to the pinned upstream runtime.")
