global_dir_check(){
    if [[ -d "housekeeping" && ! -L "housekeeping" ]]; then
        clrs
        echo "Please move this script to the root of the project folder."
        echo "Once the above is completed. Then you may run the script."
    fi
}
global_dir_check
#!/bin/bash
red=$'\e[1;31m'
grn=$'\e[1;32m'
yel=$'\e[1;33m'
blu=$'\e[1;34m'
mag=$'\e[1;35m'
cyn=$'\e[1;36m'
end=$'\e[0m'
clrs(){
    if [ "$(uname)" == "Darwin" ]; then
        clear
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        clear
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        cls
    else
        clear
    fi
}
clrs

run_program(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew run
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew run
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew run
    else
        ./gradlew run
    fi
}
run_android(){
    clrs
    if [[ -d "android" && ! -L "android" ]]; then
        if [ "$(uname)" == "Darwin" ]; then
            ./gradlew android
        elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
            ./gradlew android
        elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
            gradlew android
        else
            ./gradlew android
        fi
    else
        echo "Could not find the android installation. Installing now."
        fetch_android
        if [ "$(uname)" == "Darwin" ]; then
            ./gradlew android
        elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
            ./gradlew android
        elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
            gradlew android
        else
            ./gradlew android
        fi
    fi
}
intellij(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew idea
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew idea
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew idea
    else
        ./gradlew idea
    fi
}
eclipse(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew eclipse
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew eclipse
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew eclipse
    else
        ./gradlew eclipse
    fi
}
assemble_project(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew assemble
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew assemble
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew assemble
    else
        ./gradlew assemble
    fi
}
build_prtoject(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew build
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew build
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew build
    else
        ./gradlew build
    fi
}
dist_project(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew distZip
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew distZip
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew distZip
    else
        ./gradlew distZip
    fi
}
fetch_android(){
    clrs
    if [ "$(uname)" == "Darwin" ]; then
        ./gradlew fetchAndroid
        cp -r ./main/res ./android/res
    elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
        ./gradlew fetchAndroid
        cp -r ./main/res ./android/res
    elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
        gradlew fetchAndroid
        cp -r ./main/res ./android/res
    else
        ./gradlew fetchAndroid
        cp -r ./main/res ./android/res
    fi
}
logo(){
cat <<'END'

          .                                                      .
        .n                   .                 .                  n.
  .   .dP                  dP                   9b                 9b.    .
 4    qXb         .       dX                     Xb       .        dXp     t
dX.    9Xb      .dXb    __                         __    dXb.     dXP     .Xb
9XXb._       _.dXXXXb dXXXXbo.                 .odXXXXb dXXXXb._       _.dXXP
 9XXXXXXXXXXXXXXXXXXXVXXXXXXXXOo.           .oOXXXXXXXXVXXXXXXXXXXXXXXXXXXXP
  `9XXXXXXXXXXXXXXXXXXXXX'~   ~`OOO8b   d8OOO'~   ~`XXXXXXXXXXXXXXXXXXXXXP'
    `9XXXXXXXXXXXP' `9XX'          `98v8P'          `XXP' `9XXXXXXXXXXXP'
        ~~~~~~~       9X.          .db|db.          .XP       ~~~~~~~
                        )b.  .dbo.dP'`v'`9b.odb.  .dX(
                      ,dXXXXXXXXXXXb     dXXXXXXXXXXXb.
                     dXXXXXXXXXXXP'   .   `9XXXXXXXXXXXb
                    dXXXXXXXXXXXXb   d|b   dXXXXXXXXXXXXb
                    9XXb'   `XXXXXb.dX|Xb.dXXXXX'   `dXXP
                     `'      9XXXXXX(   )XXXXXXP      `'
                              XXXX X.`v'.X XXXX
                              XP^X'`b   d'`X^XX
                              X. 9  `   '  P )X
                              `b  `       '  d'
                               `             '
                               
END
}
logo
showMenu(){

echo "<========================================>"
echo "    Tasks"
echo "<========================================>"
echo "[1] Generate Project - Intellij"
echo "[2] Generate Project - Eclipse"
echo
echo "[3] Run Project - Desktop"
echo "[4] Run Project - Android"
echo
echo "[5] Assemble Project"
echo "[6] Build Project"
echo "[7] Distribute to Zip"
echo
echo "[8] Fetch Android"
echo
echo "[0] exit"
echo "<========================================>"

read -p "Please Select A Number: " mc
return $mc
}


while [[ "$m" != "0" ]]
do
    if [[ "$m" == "1" ]]; then
        intellij
    elif [[ "$m" == "2" ]]; then
        eclipse
    elif [[ "$m" == "3" ]]; then
        run_program
    elif [[ "$m" == "4" ]]; then
        run_android
    elif [[ "$m" == "5" ]]; then
        assemble_project
    elif [[ "$m" == "6" ]]; then
        build_prtoject
    elif [[ "$m" == "7" ]]; then
        dist_project
    elif [[ "$m" == "8" ]]; then
        fetch_android
    fi
    echo
    echo
    logo
    showMenu
    m=$?
done

exit 0;
