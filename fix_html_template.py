
import os

file_path = 'composeApp/src/commonMain/kotlin/com/ecolix/data/services/templates/HtmlBulletinTemplate.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

# Indent (24 spaces based on view_file observation, but checking line)
# We will just iterate and find the lines content.

modified = False
for i, line in enumerate(lines):
    if '<h3>GROUPE SCOLAIRE ECOLIX</h3>' in line:
        # Check if already patched
        if '${reportCard.schoolInfo' in line:
            print("Already patched School Name")
            continue
        
        # Replace School Name
        lines[i] = line.replace('GROUPE SCOLAIRE ECOLIX', '${reportCard.schoolInfo?.schoolName ?: "GROUPE SCOLAIRE ECOLIX"}')
        print(f"Patched School Name at line {i+1}")
        modified = True

    if '<p>BP : 1234 Lomé</p>' in line:
        lines[i] = line.replace('BP : 1234 Lomé', '${reportCard.schoolInfo?.address ?: "BP : 1234 Lomé"}')
        print(f"Patched Address at line {i+1}")
        modified = True

    if '<p>Tel: 22 22 22 22</p>' in line:
        lines[i] = line.replace('Tel: 22 22 22 22', '${reportCard.schoolInfo?.phone?.let { "Tel: $it" } ?: "Tel: 22 22 22 22"}')
        print(f"Patched Phone at line {i+1}")
        modified = True
        
    if '"Discipline - Travail - Succès"' in line:
         lines[i] = line.replace('"Discipline - Travail - Succès"', '"${reportCard.schoolInfo?.schoolSlogan ?: "Discipline - Travail - Succès"}"')
         print(f"Patched Slogan at line {i+1}")
         modified = True

    # Column 1
    if '<h3>DRE-MARITIME</h3>' in line:
        lines[i] = line.replace('DRE-MARITIME', '${reportCard.schoolInfo?.educationDirection ?: "DRE-MARITIME"}')
        print(f"Patched DRE at line {i+1}")
        modified = True
        
    if '<h3>IESG-VOGAN</h3>' in line:
        lines[i] = line.replace('IESG-VOGAN', '${reportCard.schoolInfo?.inspection ?: "IESG-VOGAN"}')
        print(f"Patched Inspection at line {i+1}")
        modified = True

    # Column 3
    if '<h3>RÉPUBLIQUE TOGOLAISE</h3>' in line:
        lines[i] = line.replace('RÉPUBLIQUE TOGOLAISE', '${reportCard.schoolInfo?.republicName ?: "RÉPUBLIQUE TOGOLAISE"}')
        print(f"Patched Republic Name at line {i+1}")
        modified = True

    if '<p>Travail - Liberté - Patrie</p>' in line:
        lines[i] = line.replace('Travail - Liberté - Patrie', '${reportCard.schoolInfo?.republicMotto ?: "Travail - Liberté - Patrie"}')
        print(f"Patched Motto at line {i+1}")
        modified = True
        
    # Logo
    if '<div class="logo">🎓</div>' in line:
         lines[i] = line.replace('<div class="logo">🎓</div>', '<div class="logo">${if (reportCard.schoolInfo?.logoUrl != null) "<img src=\\"${reportCard.schoolInfo?.logoUrl}\\" style=\\"max-height: 50px;\\" alt=\\"Logo\\"/>" else "🎓"}</div>')
         print(f"Patched Logo at line {i+1}")
         modified = True


if modified:
    with open(file_path, 'w') as f:
        f.writelines(lines)
    print("File updated successfully.")
else:
    print("No changes needed or targets not found.")
