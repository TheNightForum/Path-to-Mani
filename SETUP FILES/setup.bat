:start
cls
echo MADE BY BRAYDEN MOON AKA CRAZYWOLF
echo>nul
echo>nul
echo>nul
echo.
echo>nul
echo>nul
echo>nul
echo>nul
echo [1] Setup for eclipse.
echo [2] Setup for intellij.
echo.
echo.
echo.
echo [8] Credits
echo [9] Exit
echo.
echo.
set /p input= You have chosen?
if %input%==1 goto eclipse if NOT goto start
if %input%==2 goto intellij if NOT goto start
if %input%==8 goto Credits if NOT goto start
if %input%==9 goto MainExit if NOT goto start
echo "%input%" is not a valid option. Please try again.
cls
echo
goto start
cls

:eclipse
cls
gradlew eclipse

:intellij
cls
gradlew idea

:Credits
cls
echo.
echo MADE BY BRAYDEN MOON AKA CRAZYWOLF
echo.
echo                  `+hdddd+`                                                `:ohdd+`
echo                 .ydhyddddy.                                            `-oyhhhhdds`
echo                -hhyysyyyhdd/`                                        `/yhhyyyyyyhdo
echo               `yhyyyyyyssyhdy:                                      -shdhysyyyysyhd:
echo               +hyyyyyyysysyhdds.                                  `/hdhysyysyyyssohy`
echo              `h/syyyyyyyyysyyhdd+`                              .+hddyysyyyysyyo:./m-
echo              -h./sssysyyyyysysyhdh/`                          :ydddhssyyyyyyys+....h+
echo              :y..+ysyysyysooooosyddy:`      /` `  `.   `:`  `odddho+++ossssyyh-....yy`
echo              -y.../shhhyyso+++++osdddh+-:y-odh-y+/yh.`+hd.-+hdddy+++++++osyhddh....s+/`
echo              .y..../ddddyo+++++++ohdddddddddddddddddyydddhdddddho+++++++++oyddd+`..s-:
echo             .oo-.../yddhyo+++++++sdddddddddhyhddhddddddddddddddho+++++++oosyydds..-+.:
echo             -+-/`..:sddyo+++++++ohdddddddddddyyhyyhhddddddddddddy+++++++osyhddh/..:--+.
echo             +o.-...-syddyo+++++oddhhyyyyyyyyhyyysyyyhhyyyyyyyyyhdds++++++oshdhs:..-`.+-
echo             -+.....-oyhdhs++++oyyyysyyyyyyyssyyyysyyyyyyyyyyyssssyyhyo++oyddyys/.....::
echo             `/...../syyhddyosyyysyyyyyyyyyyssyhyyyhyyhyyyyyyyssysyyyyhddddyysys-.....-/`
echo             `+:...:+syyyhdddhyysysyyyyyyyyssssyhhhhhhhhyyyyyyyyyyyyysyydddyyyyso:....//`
echo            .++-../+syyhhddhyyyysyyyyysyysssosyyhhhhhhhhyyyssyyyyyyyyssssyhhhysyso/-...++.
echo           `-:/..-+syyddhyyyysyyyyyyyyysssooyyhhhhhhhhhhhyyyyyyyyyyyyysssoosyhhyyyso+++:.`
echo            `--:/+yhdhhyssoosyyyyyssyyssssssyyhhhhhhhhhhyyyyyyyyyyyyyysssooooosyhhhyo+-`
echo            `/ydddddyso+///+ossyyyyyssssosssyyyhhhhhhhhhhyyyyyyyssssssssso++ossyyhddhhs+-`
echo          `/shhdddhys+/::/:/+osssyysso+o++++oyyyyyyyhyyyyyysyssoosssysso+/////+ssyyhdddyo-`
echo           `:ydddhys+::/://+syysysysyso++++++ssysssyyyysyyyso+++sssysyss+/:::/:+sssyhdddho/`
echo          -ohdddyyso+oo/osssysssooooooso+++++ossyoosossssss+++++oyssyyyyso+//:///ossyhddhs/.
echo         :osyddyyyssyyssyysso++/:----:///+++++ssyo+++oooss++/::/+////+ossysss+oso+sssydddyo:
echo          `/yhysyyyssssssysso++/-........:/+++sss++++++syo+:.........-:+osysyyssyyyysyydddo:`
echo         .sddyysyyyyysssssssss+:..:oyyso:..:++ss+++++++ss+:../oyyy+-./++oosyyysssyyysyydddyo-
echo        `:oddyyyyyyyyyyyssso+/-.:sNhsdsyms..:+o++++++++os/.-hmsmyodmo:-/ossysysyyyyyysshddh/-
echo       `:oydhyyyyyyyyyysso++/:..-/ddooohNMy-.++++++++++++::dMNyoosmy:-.-/+osyyyyyyyysyyhddmdo-
echo     `-+shhhhhyyyyyssssys+++++/:.../sss+om+../+++++o++++:..yd+osso:...//+ossyyyyyyyssyyyyy/:/o/`
echo     `--::/+syyyyyyssssss++++++/-........:////++osssss++//:-:........:++++oyyyyyyyyssyo/-:--` ``
echo      .-:+syyyyssssyyyyys/:/+++++:--..--/++++++ossyyyyso++++//:--.-:/+++o++ssyyyssssyyss:--.``
echo     `.-+ossssyysssssyysss/.:+oooo+++++++++++++oyyyyyyso+++++oo+++ooooo+/:osssssyyyyssoss:--.`
echo     `-:::/oosysyyysssoo::/-..-/++ooooooo+++/++sysyyysyo+++ooooooo++//:.-/+:++osssssys+/:-...`
echo     ..-/o+++osyysssoooo+-........--:oooo++++++ssyyyyyso++++ooooo/.....--../+ooooosyyssso:-`
echo      `-:--:/+sssssooooooo+:......../ooo//////+syyyyyyys+++++++ooo.......:++ooooosssyyyo/:--.
echo      .----:+osyssssssoooooo+/-...../o++++///+syysysysyys+++++++++.....:++ooooooosyyyyssso:--.
echo     `---/osyyyyyyyssoooooooooo/-.`.:/++/////+oyysssssyyso++++++-:...:/ooooooooosssssyo::/+:.-`
echo     .--+sssssssyssoooooooooooooo....:+//:---/ssss+:-+ssss/-://:..../oooooooooossyyysys+---.
echo     `.++/::-:+ssssssoooooooooooo-....:......//:-......-:::........./ooooooooooossssyys+----
echo      ``----+syyyysssssoooooooooo-...........-+syhyssyhhy/..........-ooooooooossssyssyss/-..`
echo       `-.:ssssyyyysssssooooooooo-.........-yNMNNNNNmmmNMMd-.........+ooossssoossyyysssss-`
echo        `/sysysyysssooooooooooooo:.-s:....`yNMMmdmNNmmdmMMNs.....:s../ooooosssssyyyyyyyo:-.
echo        /yysyyyssoooooooooooooooo+./Nm-...`ymMMMMMMMMMMMMNmy`...-NN-.+ooooooosysssyyyysy--`
echo       -ss+ssssoosssoooooooooooooo:./hs....-mmNMMMMMMMMMNmmo....yh/-+oooooooosys-::+ossy..
echo       -. :/-` `:+soooooooooooooooo/-.+/....:dNNNMMMMMNNNms....+/./ooooooosssoss     `-/`
echo               ````.:oooooooooooooooo/.:/.....+sdNNmNmds/-..../::+oooooooosss:.-
echo                   `++osoooooooooooooo+--//-.....-/y/-.....-//-/oooooooooos/.-
echo                     .+o+++ooooooooooooo/-.::+ssyhdNdysso+/:-/oooooooo+++o/-
echo                     ``   `/-:oo+ooooooooo+:...:/oosoo+:..-/oooooooo/o-`-`-
echo                           ` `+.`:o::oo+/oo++//:::---:::/+oooooo+oo+`..
echo                              `   `  //` :++/ooooooooooooooo-.++ `-.
echo                                          .: .:++-+oo/.`:o/`  `-

echo.
echo [1] Back To Menu
echo [2] Exit
echo.
set /p op=Your Choice?
if %op%==1 goto start
if %op%==2 goto MainExit
pause>nul
del "nul"

:MainExit
cls
echo.
echo MADE BY BRAYDEN MOON AKA CRAZYWOLF
echo.
echo                  `+hdddd+`                                                `:ohdd+`
echo                 .ydhyddddy.                                            `-oyhhhhdds`
echo                -hhyysyyyhdd/`                                        `/yhhyyyyyyhdo
echo               `yhyyyyyyssyhdy:                                      -shdhysyyyysyhd:
echo               +hyyyyyyysysyhdds.                                  `/hdhysyysyyyssohy`
echo              `h/syyyyyyyyysyyhdd+`                              .+hddyysyyyysyyo:./m-
echo              -h./sssysyyyyysysyhdh/`                          :ydddhssyyyyyyys+....h+
echo              :y..+ysyysyysooooosyddy:`      /` `  `.   `:`  `odddho+++ossssyyh-....yy`
echo              -y.../shhhyyso+++++osdddh+-:y-odh-y+/yh.`+hd.-+hdddy+++++++osyhddh....s+/`
echo              .y..../ddddyo+++++++ohdddddddddddddddddyydddhdddddho+++++++++oyddd+`..s-:
echo             .oo-.../yddhyo+++++++sdddddddddhyhddhddddddddddddddho+++++++oosyydds..-+.:
echo             -+-/`..:sddyo+++++++ohdddddddddddyyhyyhhddddddddddddy+++++++osyhddh/..:--+.
echo             +o.-...-syddyo+++++oddhhyyyyyyyyhyyysyyyhhyyyyyyyyyhdds++++++oshdhs:..-`.+-
echo             -+.....-oyhdhs++++oyyyysyyyyyyyssyyyysyyyyyyyyyyyssssyyhyo++oyddyys/.....::
echo             `/...../syyhddyosyyysyyyyyyyyyyssyhyyyhyyhyyyyyyyssysyyyyhddddyysys-.....-/`
echo             `+:...:+syyyhdddhyysysyyyyyyyyssssyhhhhhhhhyyyyyyyyyyyyysyydddyyyyso:....//`
echo            .++-../+syyhhddhyyyysyyyyysyysssosyyhhhhhhhhyyyssyyyyyyyyssssyhhhysyso/-...++.
echo           `-:/..-+syyddhyyyysyyyyyyyyysssooyyhhhhhhhhhhhyyyyyyyyyyyyysssoosyhhyyyso+++:.`
echo            `--:/+yhdhhyssoosyyyyyssyyssssssyyhhhhhhhhhhyyyyyyyyyyyyyysssooooosyhhhyo+-`
echo            `/ydddddyso+///+ossyyyyyssssosssyyyhhhhhhhhhhyyyyyyyssssssssso++ossyyhddhhs+-`
echo          `/shhdddhys+/::/:/+osssyysso+o++++oyyyyyyyhyyyyyysyssoosssysso+/////+ssyyhdddyo-`
echo           `:ydddhys+::/://+syysysysyso++++++ssysssyyyysyyyso+++sssysyss+/:::/:+sssyhdddho/`
echo          -ohdddyyso+oo/osssysssooooooso+++++ossyoosossssss+++++oyssyyyyso+//:///ossyhddhs/.
echo         :osyddyyyssyyssyysso++/:----:///+++++ssyo+++oooss++/::/+////+ossysss+oso+sssydddyo:
echo          `/yhysyyyssssssysso++/-........:/+++sss++++++syo+:.........-:+osysyyssyyyysyydddo:`
echo         .sddyysyyyyysssssssss+:..:oyyso:..:++ss+++++++ss+:../oyyy+-./++oosyyysssyyysyydddyo-
echo        `:oddyyyyyyyyyyyssso+/-.:sNhsdsyms..:+o++++++++os/.-hmsmyodmo:-/ossysysyyyyyysshddh/-
echo       `:oydhyyyyyyyyyysso++/:..-/ddooohNMy-.++++++++++++::dMNyoosmy:-.-/+osyyyyyyyysyyhddmdo-
echo     `-+shhhhhyyyyyssssys+++++/:.../sss+om+../+++++o++++:..yd+osso:...//+ossyyyyyyyssyyyyy/:/o/`
echo     `--::/+syyyyyyssssss++++++/-........:////++osssss++//:-:........:++++oyyyyyyyyssyo/-:--` ``
echo      .-:+syyyyssssyyyyys/:/+++++:--..--/++++++ossyyyyso++++//:--.-:/+++o++ssyyyssssyyss:--.``
echo     `.-+ossssyysssssyysss/.:+oooo+++++++++++++oyyyyyyso+++++oo+++ooooo+/:osssssyyyyssoss:--.`
echo     `-:::/oosysyyysssoo::/-..-/++ooooooo+++/++sysyyysyo+++ooooooo++//:.-/+:++osssssys+/:-...`
echo     ..-/o+++osyysssoooo+-........--:oooo++++++ssyyyyyso++++ooooo/.....--../+ooooosyyssso:-`
echo      `-:--:/+sssssooooooo+:......../ooo//////+syyyyyyys+++++++ooo.......:++ooooosssyyyo/:--.
echo      .----:+osyssssssoooooo+/-...../o++++///+syysysysyys+++++++++.....:++ooooooosyyyyssso:--.
echo     `---/osyyyyyyyssoooooooooo/-.`.:/++/////+oyysssssyyso++++++-:...:/ooooooooosssssyo::/+:.-`
echo     .--+sssssssyssoooooooooooooo....:+//:---/ssss+:-+ssss/-://:..../oooooooooossyyysys+---.
echo     `.++/::-:+ssssssoooooooooooo-....:......//:-......-:::........./ooooooooooossssyys+----
echo      ``----+syyyysssssoooooooooo-...........-+syhyssyhhy/..........-ooooooooossssyssyss/-..`
echo       `-.:ssssyyyysssssooooooooo-.........-yNMNNNNNmmmNMMd-.........+ooossssoossyyysssss-`
echo        `/sysysyysssooooooooooooo:.-s:....`yNMMmdmNNmmdmMMNs.....:s../ooooosssssyyyyyyyo:-.
echo        /yysyyyssoooooooooooooooo+./Nm-...`ymMMMMMMMMMMMMNmy`...-NN-.+ooooooosysssyyyysy--`
echo       -ss+ssssoosssoooooooooooooo:./hs....-mmNMMMMMMMMMNmmo....yh/-+oooooooosys-::+ossy..
echo       -. :/-` `:+soooooooooooooooo/-.+/....:dNNNMMMMMNNNms....+/./ooooooosssoss     `-/`
echo               ````.:oooooooooooooooo/.:/.....+sdNNmNmds/-..../::+oooooooosss:.-
echo                   `++osoooooooooooooo+--//-.....-/y/-.....-//-/oooooooooos/.-
echo                     .+o+++ooooooooooooo/-.::+ssyhdNdysso+/:-/oooooooo+++o/-
echo                     ``   `/-:oo+ooooooooo+:...:/oosoo+:..-/oooooooo/o-`-`-
echo                           ` `+.`:o::oo+/oo++//:::---:::/+oooooo+oo+`..
echo                              `   `  //` :++/ooooooooooooooo-.++ `-.
echo                                          .: .:++-+oo/.`:o/`  `-

pause >nul
del "nul"
exit