
build:
	@./gradlew jar

install: build
	@sudo mkdir -p /opt/anis/eventb_cond_extract/lib /opt/anis/eventb_cond_extract/bin
	@sudo cp ./build/libs/eventb-cond-extract.jar /opt/anis/eventb_cond_extract/lib/
	@sudo cp ./scripts/eventb_cond_extract /opt/anis/eventb_cond_extract/bin/

.PHONY: build install
