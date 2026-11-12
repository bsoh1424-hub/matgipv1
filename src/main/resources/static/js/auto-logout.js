/**
 * 5분 무입력/무활동 자동 로그아웃 스크립트
 * - 300초(5분) 동안 사용자 입력(키보드, 마우스, 터치, 스크롤)이 없으면 자동 로그아웃
 * - 활동 감지 시 남은 시간 5분으로 자동 리셋
 */
(function() {
    const TIMEOUT_SECONDS = 300; // 5분 = 300초
    let remainingSeconds = TIMEOUT_SECONDS;
    let timerInterval = null;

    function formatTime(seconds) {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    }

    function updateBadge() {
        const badge = document.getElementById('sessionTimerBadge');
        if (badge) {
            badge.innerText = "⏱️ " + formatTime(remainingSeconds);
            if (remainingSeconds <= 60) {
                badge.classList.remove('bg-light', 'text-dark');
                badge.classList.add('bg-danger', 'text-white');
            } else {
                badge.classList.remove('bg-danger', 'text-white');
                badge.classList.add('bg-light', 'text-dark');
            }
        }
    }

    function resetTimer() {
        remainingSeconds = TIMEOUT_SECONDS;
        updateBadge();
    }

    function onTimeout() {
        if (timerInterval) clearInterval(timerInterval);
        alert("⚠️ 5분 동안 아무런 활동이 없어 안전을 위해 자동으로 로그아웃되었습니다.");
        window.location.href = "/member/logout";
    }

    function startTimer() {
        updateBadge();
        timerInterval = setInterval(function() {
            remainingSeconds--;
            updateBadge();
            if (remainingSeconds <= 0) {
                onTimeout();
            }
        }, 1000);

        // 사용자 활동 감지 이벤트 리스너 등록
        const events = ['mousemove', 'keydown', 'mousedown', 'touchstart', 'scroll', 'click'];
        events.forEach(function(evt) {
            window.addEventListener(evt, resetTimer, { passive: true });
        });
    }

    // DOM 로드 완료 후 실행
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', startTimer);
    } else {
        startTimer();
    }
})();
