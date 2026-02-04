import sys

def replace_in_file(filename):
    with open(filename, 'r') as f:
        content = f.read()

    # 1. Update isError for endDate (already done for some, but let's be sure)
    content = content.replace(
        'isError = endError != null,',
        'isError = endError != null || (if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) != null else false),'
    )

    # 2. Update supportingText for endDate
    old_supporting = 'supportingText = { endError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },'
    new_supporting = """supportingText = { 
                            val rangeError = if (validateDate(startDate) == null && validateDate(endDate) == null) validateDateRange(normalizeDate(startDate), normalizeDate(endDate)) else null
                            endError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            ?: rangeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },"""
    content = content.replace(old_supporting, new_supporting)

    # 3. Update onValueChange for startDate in NewSchoolYearDialog (line 895ish)
    # We'll be careful here to only target the one that doesn't have it yet.
    # Note: NewSchoolYearDialog usually uses startDate = it; isManualEditing = false
    # I'll target the pattern specifically for NewSchoolYearDialog if possible, 
    # but the simplest is to replace the one that doesn't have isManualEditing yet.
    
    content = content.replace(
        'onValueChange = { startDate = it },',
        'onValueChange = { startDate = it; isManualEditing = false },'
    )
    content = content.replace(
        'onValueChange = { endDate = it },',
        'onValueChange = { endDate = it; isManualEditing = false },'
    )

    with open(filename, 'w') as f:
        f.write(content)

if __name__ == "__main__":
    replace_in_file(sys.argv[1])
