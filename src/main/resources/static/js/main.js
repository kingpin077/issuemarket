document.addEventListener('DOMContentLoaded', function() {
    // 메뉴 버튼, 사이드바, 오버레이, 타이틀, 홈 버튼 요소 참조
    var menuButton = document.getElementById('menu-button');
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('overlay');
    var issueMarketTitle = document.getElementById('issue-market-title');
    var homeButton = document.getElementById('home-button');

    // 메뉴 버튼 클릭 시 사이드바 및 오버레이 표시 토글
    menuButton.addEventListener('click', function() {
        sidebar.classList.toggle('sidebar-visible');
        overlay.classList.toggle('overlay-visible');
    });

    // 오버레이 클릭 시 사이드바 및 오버레이 닫기
    overlay.addEventListener('click', function() {
        sidebar.classList.remove('sidebar-visible');
        overlay.classList.remove('overlay-visible');
    });

    // 배우 버튼 클릭 시 '/actor' 페이지로 이동
    document.getElementById('actor-button').addEventListener('click', function() {
        window.location.href = '/actor';
    });

    // 웹툰 버튼 클릭 시 '/webtoon' 페이지로 이동
    document.getElementById('webtoon-button').addEventListener('click', function() {
        window.location.href = '/webtoon';
    });

    // 타이틀 클릭 시 페이지 새로고침
    issueMarketTitle.addEventListener('click', function() {
        location.reload();
    });

    // 홈 버튼 클릭 시 페이지 새로고침
    homeButton.addEventListener('click', function() {
        location.reload();
    });

    // 키워드 버튼 클릭 시 검색 입력 필드에 해당 키워드를 설정하고 제출
    document.querySelectorAll('button[data-keyword]').forEach(function(button) {
        button.addEventListener('click', function() {
            var keyword = button.getAttribute('data-keyword');
            submitKeyword(keyword);
        });
    });

    // 검색어를 설정하고 폼을 제출하는 함수
    window.submitKeyword = function(keyword) {
        document.getElementById('keyword_search').value = keyword;
        document.getElementById('keywordForm').submit();
    };
});

// 전환 상태 변수
let isHomepageVisible = true;

// 버튼 클릭 이벤트 리스너
document.getElementById("toggleButton").addEventListener("click", function() {
    if (isHomepageVisible) {
        // 메인 콘텐츠 숨기기
        gsap.to("#homepage", {
            duration: 1,
            opacity: 0,
            onComplete: function() {
                document.getElementById("homepage").style.display = "none";
            }
        });

        // 워드 클라우드 보이기
        document.getElementById("wordCloudContainer").style.display = "block";
        gsap.fromTo("#wordCloudContainer", { opacity: 0 }, { opacity: 1, duration: 1 });

        // 버튼 텍스트 변경
        this.textContent = "메인 콘텐츠 보기";
    } else {
        // 워드 클라우드 숨기기
        gsap.to("#wordCloudContainer", {
            duration: 1,
            opacity: 0,
            onComplete: function() {
                document.getElementById("wordCloudContainer").style.display = "none";
            }
        });

        // 메인 콘텐츠 보이기
        document.getElementById("homepage").style.display = "block";
        gsap.fromTo("#homepage", { opacity: 0 }, { opacity: 1, duration: 1 });

        // 버튼 텍스트 변경
        this.textContent = "워드 클라우드 보기";
    }
    isHomepageVisible = !isHomepageVisible;
});
