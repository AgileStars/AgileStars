"""Merge the two dragon textures into one, and produce a bbmodel that uses it.

Bedrock/GeckoLib geo.json supports a single texture per model, but this project
has two (body 1024x512 + wings 512x512). The merged texture stacks them:
  body  -> y   0..512   (UV unchanged)
  wings -> y 512..1024  (wing UVs get +512 on v)
"""
import base64
import copy
import io
import json
import os

from PIL import Image

WS = r"C:\Users\klkak\Documents\deepseek-harness\default-workspace"
SRC = r"D:\D.bbmodel"
OUT_DIR = os.path.join(WS, "assets", "source-models")
os.makedirs(OUT_DIR, exist_ok=True)

data = json.loads(open(SRC, encoding="utf8", errors="replace").read())


def decode(tex):
    src = tex.get("source", "")
    return Image.open(io.BytesIO(base64.b64decode(src.split(",", 1)[1]))).convert("RGBA")


body = decode(data["textures"][0])
wings = decode(data["textures"][1])
print("body art :", body.size)
print("wings art:", wings.size)

MERGED_W, MERGED_H = 1024, 1024
merged = Image.new("RGBA", (MERGED_W, MERGED_H), (0, 0, 0, 0))
merged.paste(body, (0, 0))
merged.paste(wings, (0, 512))
merged_path = os.path.join(OUT_DIR, "D_merged.png")
merged.save(merged_path)
print("merged texture ->", merged_path, merged.size)

# --- rewrite the project -----------------------------------------------------
merged_data = copy.deepcopy(data)

# every face switches to texture 0 ...
switched = 0
for e in merged_data["elements"]:
    for f in (e.get("faces") or {}).values():
        if f.get("texture") == 1:
            f["texture"] = 0
            switched += 1
            # ... and wing UVs move down into the wings half
            for k, uv in (f.get("uv") or {}).items():
                f["uv"][k] = [uv[0], uv[1] + 512]
print("faces repointed from texture 1 -> 0:", switched)

# drop the second texture entry
merged_data["textures"] = [merged_data["textures"][0]]
# update the first texture to the merged image
buf = io.BytesIO()
merged.save(buf, format="PNG")
merged_data["textures"][0]["name"] = "death-dragon-merged.png"
merged_data["textures"][0]["source"] = "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()
merged_data["resolution"] = {"width": MERGED_W, "height": MERGED_H}

out = os.path.join(OUT_DIR, "D_merged.bbmodel")
with open(out, "w", encoding="utf8") as f:
    json.dump(merged_data, f, ensure_ascii=False)
print("merged project ->", out, f"{os.path.getsize(out):,} bytes")
print()
print("textures now:", [t["name"] for t in merged_data["textures"]])
print("resolution now:", merged_data["resolution"])
