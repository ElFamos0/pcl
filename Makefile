parser:
	@java -jar ./lib/antlr-4.9.2-complete.jar ./expr.g4 -no-listener -visitor -o ./src/parser
	@echo "Number of adaptive prediction: $$(cat ./src/parser/exprParser.java | grep adaptive | wc -l)"

compile:
	@javac -cp ./lib/antlr-4.9.2-complete.jar:./src ./src/ast/*.java -d ./bin
	@javac -cp ./lib/antlr-4.9.2-complete.jar:./src ./src/graphViz/*.java -d ./bin
	@javac -cp ./lib/antlr-4.9.2-complete.jar:./src ./src/Main.java -d ./bin

run:
	@java -cp ./lib/antlr-4.9.2-complete.jar:./bin Main $(target)

svg: 
	@dot -Tsvg ./out/tree.dot -o ./out/tree.svg
	@inkscape ./out/tree.svg
	
all: parser compile run 
test: run 