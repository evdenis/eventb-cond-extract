
build:
	@./gradlew jar

install: build
	@sudo mkdir -p /opt/isp-rtv/eventb_cond_extract/lib /opt/isp-rtv/eventb_cond_extract/bin
	@sudo cp ./build/libs/eventb-cond-extract.jar /opt/isp-rtv/eventb_cond_extract/lib/
	@sudo cp ./scripts/eventb_cond_extract /opt/isp-rtv/eventb_cond_extract/bin/

.PHONY: build install
