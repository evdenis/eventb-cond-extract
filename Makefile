
build:
	@./gradlew jar

install: build
	@sudo mkdir -p /opt/eventb_cond_extract/lib /opt/eventb_cond_extract/bin
	@sudo cp ./build/libs/eventb-cond-extract.jar /opt/eventb_cond_extract/lib/
	@sudo cp ./scripts/eventb_cond_extract /opt/eventb_cond_extract/bin/

.PHONY: build install
