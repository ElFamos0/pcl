# Shell script that compiles and links every .s file in the current directory

# as -o program.o -c test.s
# ld -o program -lc program.o

for file in *.s
do
    echo "Compiling $file..."
    as -o ${file%.*}.o -c $file
    ld -o ${file%.*} -lc ${file%.*}.o
done