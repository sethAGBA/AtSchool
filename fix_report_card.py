
import os

file_path = 'composeApp/src/commonMain/kotlin/com/ecolix/presentation/screens/notes/tabs/bulletins/ReportCardView.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

# Indentation (36 spaces)
indent = ' ' * 36

# Check if already patched to avoid duplication
if any('val devoirs =' in line for line in lines):
    print("File already patched.")
    exit(0)

# Find line with Text(subject.name...
start_index = -1
for i, line in enumerate(lines):
    if 'Text(subject.name' in line and 'Modifier.weight(2.2f)' in line:
        start_index = i
        break

if start_index != -1:
    # Insert variables before this line
    insertion = [
        f'{indent}val devoirs = subject.evaluations.filter {{ it.typeName == "Devoir" }}.joinToString(", ") {{ it.mark.toString() }}\n',
        f'{indent}val composition = subject.evaluations.find {{ it.typeName == "Composition" }}?.mark?.toString() ?: "-"\n',
        '\n'
    ]
    lines[start_index:start_index] = insertion
    print(f"Inserted variables at line {start_index+1}")

# Replace usages
for i, line in enumerate(lines):
    if 'subject.devoir?.toString()' in line:
        lines[i] = f'{indent}Text(if (devoirs.isNotEmpty()) devoirs else "-", modifier = Modifier.weight(0.7f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)\n'
        print(f"Replaced usage at line {i+1}")
    if 'subject.composition?.toString()' in line:
        lines[i] = f'{indent}Text(composition, modifier = Modifier.weight(0.7f), fontSize = 11.sp, textAlign = TextAlign.Center, color = colors.textPrimary)\n'
        print(f"Replaced usage at line {i+1}")

with open(file_path, 'w') as f:
    f.writelines(lines)
