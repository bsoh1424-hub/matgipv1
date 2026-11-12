// ===== 요소 가져오기 =====
const modal = document.getElementById('gachaModal');
const openBtn = document.getElementById('openGachaBtn');
const closeBtn = document.getElementById('closeGachaBtn');
const drawBtn = document.getElementById('drawBtn');
const stage = document.getElementById('gachaStage');
const stageText = document.getElementById('stageText');
const resultArea = document.getElementById('resultArea');

// 기본 맛집 대표 이미지 (DB에 이미지가 없거나 로딩 실패 시 사용)
const DEFAULT_PLACE_IMAGE = 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=600&q=80';

// ===== 상태 =====
let selectedCategory = '';
let isDrawing = false;   // 뽑는 중에 버튼을 또 누르는 것을 막기 위한 플래그
let hasDrawn = false;    // 한 번이라도 뽑았는지
let messageTimer = null; // 뽑는 중 문구 순환 타이머
const historyData = [];

const STAGE_MESSAGES = ['캡슐을 돌리는 중…', '두근두근…', '뭐가 나올까요? 🤞', '거의 다 나왔어요!'];

// ===== 모달 열기/닫기 및 초기화 =====
if (openBtn) {
    openBtn.addEventListener('click', () => {
        if (modal) modal.style.display = 'flex';
    });
}

// 닫기 함수: 모달 닫기 시 가챠 결과 및 이력 리스트 전체 초기화
function closeAndResetGacha() {
    if (modal) {
        modal.style.display = 'none';
    }

    // 1. 뽑는 중 연출 및 타이머 중단
    stopStage();
    isDrawing = false;
    hasDrawn = false;

    // 2. 결과 카드 영역 초기화 및 숨김
    if (resultArea) {
        resultArea.classList.add('hidden');
        resultArea.classList.remove('reveal');
        const imgContainer = document.getElementById('resultImgContainer');
        if (imgContainer) imgContainer.classList.add('hidden');
    }

    // 3. 최근 가챠 이력 리스트 전체 초기화
    historyData.length = 0;
    const historyContainer = document.getElementById('historyList');
    if (historyContainer) {
        historyContainer.innerHTML = '';
    }

    // 4. 지역구 필터 셀렉트박스 초기화
    const guSelect = document.getElementById('guSelect');
    if (guSelect) {
        guSelect.value = '';
    }

    // 5. 카테고리 선택 버튼 초기화
    selectedCategory = '';
    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));

    // 6. 가챠 버튼 초기화
    if (drawBtn) {
        drawBtn.disabled = false;
        drawBtn.innerHTML = '🎲 가챠 뽑기';
    }

    // 7. 폭죽 파티클 레이어 정리
    const layer = document.getElementById('confettiLayer');
    if (layer) {
        layer.innerHTML = '';
    }
}

if (closeBtn) {
    closeBtn.addEventListener('click', closeAndResetGacha);
}

// 팝업 바깥(어두운 배경)을 클릭하면 닫기 및 초기화
window.addEventListener('click', (e) => {
    if (e.target === modal) {
        closeAndResetGacha();
    }
});

// ===== 카테고리 토글 버튼 =====
document.querySelectorAll('.category-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        if (selectedCategory === this.dataset.value) {
            // 이미 선택된 카테고리 재클릭 시 해제
            selectedCategory = '';
            this.classList.remove('active');
        } else {
            document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            selectedCategory = this.dataset.value;
        }
    });
});

// ===== 유틸 =====
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

function esc(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

// ===== 뽑는 중 연출 시작/종료 =====
function startStage() {
    let i = 0;
    if (stageText) stageText.textContent = STAGE_MESSAGES[0];
    if (stage) stage.classList.remove('open', 'hidden');
    messageTimer = setInterval(() => {
        i = (i + 1) % STAGE_MESSAGES.length;
        if (stageText) stageText.textContent = STAGE_MESSAGES[i];
    }, 700);
}

function stopStage() {
    if (messageTimer) clearInterval(messageTimer);
    if (stage) {
        stage.classList.add('hidden');
        stage.classList.remove('open');
    }
}

// ===== 가챠 뽑기 실행 (SQL busan_restaurant 테이블 기반) =====
if (drawBtn) {
    drawBtn.addEventListener('click', async () => {
        if (isDrawing) return;
        isDrawing = true;
        drawBtn.disabled = true;
        drawBtn.innerHTML = '🎰 뽑는 중...';

        // 이전 결과는 숨기고 캡슐 연출 시작
        if (resultArea) {
            resultArea.classList.add('hidden');
            resultArea.classList.remove('reveal');
        }
        startStage();

        const guSelect = document.getElementById('guSelect');
        const guName = guSelect ? guSelect.value : '';

        try {
            const queryParams = new URLSearchParams();
            if (guName) queryParams.append('guName', guName);
            if (selectedCategory) queryParams.append('category', selectedCategory);

            // 서버 응답과 연출 시간 병렬 처리 (최소 1.5초)
            const [response] = await Promise.all([
                fetch(`/api/gacha/draw?${queryParams.toString()}`),
                sleep(1500)
            ]);

            if (!response.ok) {
                stopStage();
                alert('해당 조건에 맞는 맛집이 없습니다. 지역구나 카테고리를 변경해 보세요!');
                return;
            }

            const result = await response.json();

            // 캡슐 오픈 연출
            if (messageTimer) clearInterval(messageTimer);
            if (stageText) stageText.textContent = '짠! 🎉';
            if (stage) stage.classList.add('open');
            await sleep(600);
            stopStage();

            // busan_restaurant DB 결과 공개
            showResult(result);
            launchConfetti();
            hasDrawn = true;

            // 최근 뽑기 이력 추가 (최대 3개 유지)
            historyData.unshift(result);
            if (historyData.length > 3) historyData.pop();
            renderHistory();

        } catch (error) {
            console.error('가챠 에러:', error);
            stopStage();
            alert('가챠를 진행하는 중 오류가 발생했습니다.');
        } finally {
            isDrawing = false;
            drawBtn.disabled = false;
            drawBtn.innerHTML = hasDrawn ? '🎲 다시 뽑기' : '🎲 가챠 뽑기';
        }
    });
}

// ===== DB 식당 결과 카드 바인딩 및 표시 =====
function showResult(result) {
    const tagEl = document.getElementById('resultTag');
    const nameEl = document.getElementById('resultName');
    const descEl = document.getElementById('resultDesc');
    const menuEl = document.getElementById('resultMenu');

    if (tagEl) tagEl.innerText = `${result.guName || '부산'} · ${result.category || '맛집'}`;
    if (nameEl) nameEl.innerText = result.restaurantNameKo || '맛있는 식당';
    if (descEl) descEl.innerText = result.summaryKo || result.address || '소개글이 없습니다.';
    if (menuEl) menuEl.innerText = result.mainMenu || '대표 메뉴 정보 없음';

    // 상세페이지 이동 링크 연결
    const linkEl = document.getElementById('resultDetailLink');
    if (linkEl && (result.pno || result.id)) {
        const placeNo = result.pno || result.id;
        linkEl.href = `/place/read?pno=${placeNo}`;
        linkEl.classList.remove('hidden');
    }

    // 실제 DB의 맛집 사진(imageUrl1) 바인딩 및 fallback 처리
    const imgContainer = document.getElementById('resultImgContainer');
    const imgEl = document.getElementById('resultImg');

    if (imgEl && imgContainer) {
        imgEl.onerror = () => {
            imgEl.onerror = null;
            imgEl.src = DEFAULT_PLACE_IMAGE;
        };

        if (result.imageUrl1 && result.imageUrl1.trim() !== '') {
            imgEl.src = result.imageUrl1.trim();
        } else if (result.imageUrl2 && result.imageUrl2.trim() !== '') {
            imgEl.src = result.imageUrl2.trim();
        } else {
            imgEl.src = DEFAULT_PLACE_IMAGE;
        }
        imgContainer.classList.remove('hidden');
    }

    if (resultArea) {
        resultArea.classList.remove('hidden');
        void resultArea.offsetWidth; // 애니메이션 리셋용
        resultArea.classList.add('reveal');
        resultArea.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

// ===== 축하 폭죽 효과 =====
function launchConfetti() {
    const layer = document.getElementById('confettiLayer');
    if (!layer) return;
    const colors = ['#ff6a3d', '#ff4d6d', '#ffc53d', '#36cfc9', '#7c5cff'];

    for (let i = 0; i < 36; i++) {
        const piece = document.createElement('span');
        piece.className = 'confetti';
        piece.style.background = colors[i % colors.length];
        piece.style.setProperty('--x', `${(Math.random() * 2 - 1) * 170}px`);
        piece.style.setProperty('--y', `${-(110 + Math.random() * 170)}px`);
        piece.style.setProperty('--r', `${Math.random() * 540 - 270}deg`);
        piece.style.animationDelay = `${Math.random() * 0.12}s`;
        layer.appendChild(piece);
        setTimeout(() => piece.remove(), 1700);
    }
}

// ===== 최근 뽑은 DB 맛집 이력 목록 출력 =====
function renderHistory() {
    const historyContainer = document.getElementById('historyList');
    if (!historyContainer) return;
    historyContainer.innerHTML = '';

    historyData.forEach(item => {
        const placeNo = item.pno || item.id;
        const historyEl = document.createElement('div');
        historyEl.className = 'history-item';
        historyEl.innerHTML = `
            <div class="history-left">
                <span class="history-gu">${esc(item.guName)}</span>
                <a href="/place/read?pno=${placeNo}" class="history-name text-decoration-none text-dark" target="_blank" title="새 탭에서 맛집 보기">
                    ${esc(item.restaurantNameKo)}
                </a>
            </div>
            <span class="history-category">${esc(item.category)}</span>
        `;
        historyContainer.appendChild(historyEl);
    });
}
