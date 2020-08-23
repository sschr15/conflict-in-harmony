import sys
import os
import zipfile
import pathlib
from urllib import request

mapping_version = sys.argv[1]
mc_version = sys.argv[2]

home = str(pathlib.Path.home())


def get_bytes_from_url(url: str) -> bytes:
    print("Downloading", url)
    with request.urlopen(url) as file:
        return file.read()


def gen_overridden_mappings(input_file: str, overriding_file: str, output_file: str):
    print("Generating modified mappings from", input_file)
    with open(input_file, "r") as file:
        original = file.read().splitlines()
        lines = []
        for i in original:
            i: str
            srg = i.split(",")[0]
            lines.append(srg)
    with open(overriding_file) as file:
        overrides = {}
        for i in file.read().splitlines():
            i: str
            srg = i.split(",")[0]
            overrides[srg] = i

    for srg, override in overrides.items():
        srg: str
        override: str
        if srg in lines:
            while srg in lines:
                original[lines.index(srg)] = override
                lines[lines.index(srg)] = "\x00"
        else:
            original.append(override)
            lines.append("\x00")

    with open(output_file, "w") as file:
        file.write("\n".join(original) + "\n")


os.makedirs("libs/mappings", exist_ok=True)
os.makedirs(f'{home}/.m2/repository/de/oceanlabs/mcp/mcp_stable/custom-{mc_version}/', exist_ok=True)

mapping_type = mapping_version.split('_')

thing = f'{mapping_type[1]}-{mc_version}'
zip_file = get_bytes_from_url(f'http://export.mcpbot.bspk.rs/mcp_{mapping_type[0]}/{thing}/mcp_{mapping_type[0]}-{thing}.zip')

with open("mapping.zip", "wb") as zf:
    zf.write(zip_file)
    del zip_file

with zipfile.ZipFile("mapping.zip") as zf:
    zf.extractall("libs/mappings")

gen_overridden_mappings("libs/mappings/fields.csv", "overridden-fields.csv", "libs/fields.csv")
gen_overridden_mappings("libs/mappings/methods.csv", "overridden-methods.csv", "libs/methods.csv")
gen_overridden_mappings("libs/mappings/params.csv", "overridden-params.csv", "libs/params.csv")

with zipfile.ZipFile(f'{home}/.m2/repository/de/oceanlabs/mcp/mcp_stable/custom-{mc_version}/mcp_stable-custom-{mc_version}.zip', "w") as file:
    file.write("libs/fields.csv", arcname="fields.csv")
    file.write("libs/methods.csv", arcname="methods.csv")
    file.write("libs/params.csv", arcname="params.csv")

print("Custom mappings generated")
