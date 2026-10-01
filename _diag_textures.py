"""Diagnose why converting D.bbmodel to a GeckoLib project loses the texture."""
import json
from collections import Counter

PATH = r"D:\D.bbmodel"
d = json.loads(open(PATH, encoding="utf8", errors="replace").read())

print("model name :", d.get("name"))
print("format     :", d.get("meta", {}).get("format_version"), d.get("meta", {}).get("model_format"))
print("resolution :", d.get("resolution"))
print()

print("=== textures ===")
for t in d.get("textures", []):
    src = t.get("source")
    kind = "EMBEDDED base64" if isinstance(src, str) and src.startswith("data:") else f"REFERENCE: {src!r}"
    print(f"  id={t.get('id')} name={t.get('name')!r}")
    print(f"     {kind}  (len={len(src) if isinstance(src, str) else 0})")
    print(f"     uv={t.get('uv')} render_mode={t.get('render_mode')} particle={t.get('particle')}")

print()
print("=== which texture does each face use? ===")
face_tex = Counter()
per_element = []
for e in d.get("elements", []):
    used = Counter()
    for fid, f in (e.get("faces") or {}).items():
        used[f.get("texture")] += 1
        face_tex[f.get("texture")] += 1
    per_element.append((e.get("name"), dict(used)))

print("  total faces per texture id:", dict(face_tex))
print()
print("  first 15 elements:")
for name, used in per_element[:15]:
    print(f"    {name!r:<28} {used}")

# are there faces with no texture assigned?
none_faces = sum(1 for e in d.get("elements", []) for f in (e.get("faces") or {}).values()
                 if f.get("texture") is None)
print()
print("  faces with texture=None:", none_faces)

print()
print("=== other project settings that affect export ===")
for k in ("uv_groups", "display", "visible_box", "parent", "bone_paths", "animations"):
    v = d.get(k)
    if k == "animations":
        print(f"  animations: {len(v or [])}")
    elif isinstance(v, (list, dict)):
        print(f"  {k}: {len(v)} entries")
    else:
        print(f"  {k}: {v!r}")
