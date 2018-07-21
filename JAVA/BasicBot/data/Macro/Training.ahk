#include, library\Gdip.ahk
#include, library\gdip_imagesearch.ahk

times:=10
pToken:=Gdip_Startup()

MsgBox,
(
Training System Started.
    Win+t : Training Multiple times
    Win+o : Training Once
    Win+c : Computer
    Win+f : Finish
)

#t::
    ; 동영상 캡쳐 프로그램(OBS)가 있으면, 동영상 캡쳐를 시작한다.
    ControlClick, x820 y676, ahk_class Qt5QWindowIcon,, LEFT

    startTime:=A_TickCount
    Loop %times% {
        training_once()
    }

    ; 동영상 캡쳐 프로그램(OBS)가 있으면, 동영상 캡쳐를 종료한다.
    ControlClick, x820 y676, ahk_class Qt5QWindowIcon,, LEFT

    duration:=A_TickCount-startTime
    MsgBox Training X %times% times has finished. Duration: %duration% ms

    return

#o::
    ; 동영상 캡쳐 프로그램(OBS)가 있으면, 동영상 캡쳐를 시작한다.
    ControlClick, x820 y676, ahk_class Qt5QWindowIcon,, LEFT

    training_once()
    duration:=A_TickCount-startTime

    ; 동영상 캡쳐 프로그램(OBS)가 있으면, 동영상 캡쳐를 종료한다.
    ControlClick, x820 y676, ahk_class Qt5QWindowIcon,, LEFT

    return

#c::
    startTime:=A_TickCount
    Loop %times% {
        computer()
    }

    duration:=A_TickCount-startTime
    MsgBox Computer X %times% times has finished. Duration: %duration% ms

    return

#f::
    ;; Exit
    Gdip_Shutdown(pToken)
    msgbox Training system has finished
    ExitApp

    return

training_once() {
    ; Run Eclipse
    ControlSend,,^{F11},workspace - Java

    ; CoordMode, Pixel, Window

    ; 이전에 진행한 게임의 Summary 화면이 띄워져 있을 수 있으므로, 단축키 alt + o를 누른다.
    ControlSend,,{Alt down}o{Alt up},ahk_class SWarClass

    ; Create Game 버튼이 나올때 까지 대기한다.
    WinGet,hwnd,ID,Brood War
    Loop {
        if (imgSearch("images\create_game.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 200
        }
    }
    ; Create Game 화면이 나오면, 단축키 alt + g를 누른다.
    ControlSend,,{Alt down}g{Alt up},ahk_class SWarClass

    ; 지도 선택 화면이 나올텐데, 단축키 alt + o를 누른다.
    Sleep, 200
    ControlSend,,{Alt down}o{Alt up},ahk_class SWarClass

    ; 게임을 시작하기 위해서 단축키 alt + o를 누른다.
    Sleep, 200
    ControlSend,,{Alt down}o{Alt up}{BACKSPACE},ahk_class SWarClass

    ; 게임이 시작될 때까지 단축키 alt + o (OK)를 누른다.
    Loop {
        if (imgSearch("images\play_screen.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 1000
            ControlSend,,{Alt down}o{Alt up}{BACKSPACE},ahk_class SWarClass
        }
    }

    ; save_replay 화면이 나올 때까지 대기한다.
    Loop {
        if (imgSearch("images\save_replay.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 200
        }
    }
}

computer() {
    ; Run Eclipse
    ControlSend,,^{F11},workspace - Java

    ; 이전에 진행한 게임의 Summary 화면이 띄워져 있을 수 있으므로, 단축키 alt + o를 누른다.
    WinActivate,ahk_class SWarClass
    ControlSend,,{Alt down}o{Alt up},ahk_class SWarClass

    ; Create Game 버튼이 나올때 까지 대기한다.
    WinGet,hwnd,ID,Brood War
    Loop {
        if (imgSearch("images\create_game.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 200
        }
    }
    ; Create Game 화면이 나오면, 단축키 alt + g를 누른다.
    ControlSend,,{Alt down}g{Alt up},ahk_class SWarClass

    ; 지도 선택 화면이 나올텐데, 단축키 alt + o를 누른다.
    Sleep, 200
    ControlSend,,{Alt down}o{Alt up},ahk_class SWarClass

    ; 아군은 테란을 선택한다.
    Sleep, 200
    MouseClickDrag, LEFT, 500, 115, 500, 175
    MouseClickDrag, LEFT, 300, 150, 300, 245
    Sleep, 800
    ; 저그 선택
    ; MouseClickDrag, LEFT, 500, 150, 500, 185
    ; 테란 선택
    ; MouseClickDrag, LEFT, 500, 150, 500, 215
    ; 프로토스 선택
    MouseClickDrag, LEFT, 500, 150, 500, 250

    ; 게임을 시작하기 위해서 단축키 alt + o를 누른다.
    Sleep, 200
    ControlSend,,{Alt down}o{Alt up}{BACKSPACE},ahk_class SWarClass

    ; 게임이 시작될 때까지 단축키 alt + o (OK)를 누른다.
    Loop {
        if (imgSearch("images\play_screen.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 1000
            ControlSend,,{Alt down}o{Alt up}{BACKSPACE},ahk_class SWarClass
        }
    }

    ; 게임 속도를 빠르게 조절한다.
    ControlSend,,{Esc},ahk_class SWarClass
    Sleep, 100
    ControlSend,,{Enter}0{Enter},ahk_class SWarClass

    ; victory 화면이 나올 때까지 대기한다.
    ; draw.bmp, defeat.bmp, victory.bmp 활용 가능.
    Loop {
        if (imgSearch("images\victory.bmp", hwnd, findX,findY)=true) {
            break
        } else {
            Sleep, 200
        }
    }
}

; 출처: http://plorence.kr/209
imgSearch(image,hwnd, byref vx, byref vy) {
    pBitmapHayStack:=Gdip_BitmapFromHWND(hwnd)
    pBitmapNeedle:=Gdip_CreateBitmapFromFile(image)

    if Gdip_ImageSearch(pBitmapHayStack,pBitmapNeedle,list,0,0,0,0,32,,1,1) {
        StringSplit, LISTArray, LIST, `, 
        vx:=LISTArray1
        vy:=LISTArray2
        Gdip_DisposeImage(pBitmapHayStack), Gdip_DisposeImage(pBitmapNeedle)
        ;Gdip_Shutdown(pToken)
        return true
    }
    else 
    {
        Gdip_DisposeImage(pBitmapHayStack), Gdip_DisposeImage(pBitmapNeedle)
        ;Gdip_Shutdown(pToken)
        return false
    }
}
