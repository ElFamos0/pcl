FROM arm32v7/alpine
COPY test.s test.s
RUN apk add --no-cache build-base
RUN as -o program.o -c test.s
RUN ld -o program program.o
CMD ["/program"]