FROM arm32v7/alpine
RUN apk add --no-cache build-base gdb
RUN cp /usr/lib/libc.so /usr/lib/ld.so.1
COPY . .
RUN sh builder.sh