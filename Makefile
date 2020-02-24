install:
	sudo apt-get update
	sudo apt-get install -y python3
	curl https://bootstrap.pypa.io/get-pip.py | sudo python3
	sudo pip3 install scbw
	scbw.play --install

build:
    git clone https://github.com/basil-ladder/sc-docker.git
    cd ./sc-docker/docker/
    docker build -f dockerfiles/wine.dockerfile -t starcraft:wine .
    docker build -f dockerfiles/bwapi.dockerfile -t starcraft:bwapi .
    docker build -f dockerfiles/play.dockerfile -t starcraft:play .
    docker build -f dockerfiles/java.dockerfile -t starcraft:java .
    pushd ../scbw/local_docker/
    curl -sL 'http://files.theabyss.ru/sc/starcraft.zip' -o starcraft.zip
    docker build -f game.dockerfile -t "starcraft:game" .
    popd
	cd ../scbw/
	pip3 install .
	scbw.play --install
    cd ~/.scbw/bots
    mkdir EmperorZerg
    mkdir EmperorZerg/AI
	mkdir EmperorZerg/read
	mkdir EmperorZerg/write
	cp ~/emperorZerg/BWAAPI.dll ./EmperorZerg/
	cp out/artifacts/emperorZerg_jar/emperorZerg.jar ~/.scbw/bots/EmperorZerg/AI/

test:
	scbw.play --bots "PurpleWave" "emperorZerg" --show_all

clean:
    pip3 uninstall scbw