#!/bin/bash
# Simple setup

PS3='Please enter your editor: '
options=("Eclipse" "Intellij" "Quit")
select opt in "${options[@]}"
do
    case $opt in
        "eclipse")
            ./gradlew eclipse
            ;;
        "intellij")
            ./gradlew idea
            ;;
        "Quit")
            break
            ;;
        *) echo invalid option;;
    esac
done
