#!/bin/bash

# All files
all_files=$(find $1 -type f | wc -l)
echo -e "All files: $all_files\n\n"

# ------------------------------------------------------------------------
# ------------------------------------------------------------------------
# ------------------------------------------------------------------------

# All source files without any postfixes
sf_no_postfixes=$(find $1 -type f -name '*.[hcS]' | wc -l)

# All source files with dot-postfixes
sf_dot_postfixes=$(find $1 -type f -name '*.[hcS].*' | wc -l)

# All source files with hyphen-postfixes
sf_hyphen_postfixes=$(find $1 -type f -name '*.[hcS]-*' | wc -l)

# All source files with underscore-postfixes
sf_underscore_postfixes=$(find $1 -type f -name '*.[hcS]_*' | wc -l)

# All source files with plus-postfixes
sf_plus_postfixes=$(find $1 -type f -name '*.[hcS]\+*' | wc -l)

# All source files with snake-postfixes
sf_snake_postfixes=$(find $1 -type f -name '*.[hcS]~*' | wc -l)

sf_sum=$((sf_no_postfixes + sf_dot_postfixes + sf_hyphen_postfixes + sf_underscore_postfixes + sf_plus_postfixes + sf_snake_postfixes))

echo "Source files (no postfixes): $sf_no_postfixes"
echo "Source files (dot-postfixes): $sf_dot_postfixes"
echo "Source files (hyphen-postfixes): $sf_hyphen_postfixes"
echo "Source files (underscore-postfixes): $sf_underscore_postfixes"
echo "Source files (plus-postfixes): $sf_plus_postfixes"
echo "Source files (snake-postfixes): $sf_snake_postfixes"
echo -e "Source files (sum): $sf_sum\n\n"

# ------------------------------------------------------------------------
# ------------------------------------------------------------------------
# ------------------------------------------------------------------------

# All build files without any postfixes
bf_no_postfixes=$(find $1 \( -name "Makefile" -o -name "Kbuild" \) | wc -l)

# All build files with dot-postfixes
bf_dot_postfixes=$(find $1 \( -name "Makefile.*" -o -name "Kbuild.*" \) | wc -l)

# All build files with hyphen-postfixes
bf_hyphen_postfixes=$(find $1 \( -name "Makefile-*" -o -name "Kbuild-*" \) | wc -l)

# All build files with underscore-postfixes
bf_underscore_postfixes=$(find $1 \( -name "Makefile_*" -o -name "Kbuild_*" \) | wc -l)

# All build files with plus-postfixes
bf_plus_postfixes=$(find $1 \( -name "Makefile\+*" -o -name "Kbuild\+*" \) | wc -l)

# All build files with snake-postfixes
bf_snake_postfixes=$(find $1 \( -name "Makefile~*" -o -name "Kbuild~*" \) | wc -l)

bf_sum=$((bf_no_postfixes + bf_dot_postfixes + bf_hyphen_postfixes + bf_underscore_postfixes + bf_plus_postfixes + bf_snake_postfixes))

echo "Build files (no postfixes): $bf_no_postfixes"
echo "Build files (dot-postfixes): $bf_dot_postfixes"
echo "Build files (hyphen-postfixes): $bf_hyphen_postfixes"
echo "Build files (underscore-postfixes): $bf_underscore_postfixes"
echo "Build files (plus-postfixes): $bf_plus_postfixes"
echo "Build files (snake-postfixes): $bf_snake_postfixes"
echo -e "Build files (sum): $bf_sum\n\n"

# ------------------------------------------------------------------------
# ------------------------------------------------------------------------
# ------------------------------------------------------------------------

# All model files without any postfixes
mf_no_postfixes=$(find $1 -type f -name 'Kconfig' | wc -l)

# All model files with dot-postfixes
mf_dot_postfixes=$(find $1 -type f -name 'Kconfig.*' | wc -l)

# All model files with hyphen-postfixes
mf_hyphen_postfixes=$(find $1 -type f -name 'Kconfig-*' | wc -l)

# All model files with underscore-postfixes
mf_underscore_postfixes=$(find $1 -type f -name 'Kconfig_*' | wc -l)

# All model files with plus-postfixes
mf_plus_postfixes=$(find $1 -type f -name 'Kconfig\+*' | wc -l)

# All model files with snake-postfixes
mf_snake_postfixes=$(find $1 -type f -name 'Kconfig~*' | wc -l)

mf_sum=$((mf_no_postfixes + mf_dot_postfixes + mf_hyphen_postfixes + mf_underscore_postfixes + mf_plus_postfixes + mf_snake_postfixes))

echo "Model files (no postfixes): $mf_no_postfixes"
echo "Model files (dot-postfixes): $mf_dot_postfixes"
echo "Model files (hyphen-postfixes): $mf_hyphen_postfixes"
echo "Model files (underscore-postfixes): $mf_underscore_postfixes"
echo "Model files (plus-postfixes): $mf_plus_postfixes"
echo "Model files (snake-postfixes): $mf_snake_postfixes"
echo -e "Model files (sum): $mf_sum\n\n"
