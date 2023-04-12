#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int main(int argc, char** argv) {
    srand(time(NULL));
    int n = rand();
    printf("%d\n", n);
    return 0;
}