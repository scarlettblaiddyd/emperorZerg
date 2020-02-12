install:
	sudo apt-get update
	sudo apt-get install -y python3
	curl https://bootstrap.pypa.io/get-pip.py | sudo python3
	sudo pip3 install scbw
	scbw.play --install

test:
	scbw.play --bots "PurpleWave" "emperorZerg" --show_all
