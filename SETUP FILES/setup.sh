#!/bin/bash
# Simple setup

PS3='Please enter your editor: '
options=("Eclipse" "Intellij" "Quit")
select opt in "${options[@]}"
do
    case $opt in
        "Option 1")
            ./gradlew eclipse
            ;;
        "Option 2")
            ./gradlew idea
            ;;
        "Quit")
            break
            ;;
        *) echo invalid option;;
    esac
done