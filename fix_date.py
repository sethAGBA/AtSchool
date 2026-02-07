
import os

file_path = 'composeApp/src/commonMain/kotlin/com/ecolix/data/services/templates/HtmlBulletinTemplate.kt'

with open(file_path, 'r') as f:
    content = f.read()

# Replace strict 24/01/2026
if '24/01/2026' in content:
    # Check if we are inside a function or a variable assignment?
    # Based on grep, it looked like a return value or assignment.
    # Let's replace instances carefully.
    
    # Instance 1: Top Right Date? No, usually date is in footer or specific location.
    # Grep said: return "24/01/2026"
    
    new_content = content.replace('"24/01/2026"', '(reportCard.generatedDate ?: "24/01/2026")')
    
    if new_content != content:
        with open(file_path, 'w') as f:
            f.write(new_content)
        print("Successfully replaced hardcoded date.")
    else:
        print("Date found but replacement resulted in no change (unlikely).")

else:
    print("Hardcoded date '24/01/2026' not found in file content.")
