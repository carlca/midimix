#!/bin/zsh

PROJECT_NAME=$(basename "$(pwd)")
echo "$PROJECT_NAME"

#bitwigutils
sudo rm -rf ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca/bitwigutils
ln -s ~/Code/Scala/bitwig/library/src/main/scala/com/carlca/bitwigutils ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca

# config
sudo rm -rf ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca/config
ln -s ~/Code/Scala/bitwig/library/src/main/scala/com/carlca/config ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca

# logger
sudo rm -rf ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca/logger
ln -s ~/Code/Scala/bitwig/library/src/main/scala/com/carlca/logger ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca

# utils
sudo rm -rf ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca/utils
ln -s ~/Code/Scala/bitwig/library/src/main/scala/com/carlca/utils ~/Code/Scala/bitwig/$PROJECT_NAME/src/main/scala/com/carlca
