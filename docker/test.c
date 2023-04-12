#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int f(int n) {
    if (n == 0) {
        return 0;
    }
    printf("%d\n", n);
    return f(n-1);
}

int main(int argc, char** argv) {
    return f(5);
}