let watchlistEventSource;
let portfolioEventSource;

function connectPortfolioSSE() {
    if (portfolioEventSource && portfolioEventSource.readyState !== portfolioEventSource.CLOSED) {
        return;
    }
    portfolioEventSource = new EventSource("/sse/connect/portfolio");
    console.log("✅ Portfolio SSE 연결됨");

    disconnectWatchlistSSE()

    portfolioEventSource.onerror = () => {
        console.warn("⚠️ Portfolio SSE 연결 오류, 연결 종료");
        portfolioEventSource.close();
        portfolioEventSource = null;
    };
}


function disconnectPortfolioSSE() {
    if (portfolioEventSource) {
        portfolioEventSource.close();
        portfolioEventSource = null;
        console.log("🔌 Portfolio SSE 연결 해제");
    }

    fetch('/sse/disconnect/portfolio', { method: 'POST' });
}

function connectWatchlistSSE() {
    if (watchlistEventSource && watchlistEventSource.readyState !== watchlistEventSource.CLOSED) {
        return;
    }
    watchlistEventSource = new EventSource("/sse/connect/watchlist");
    console.log("✅ Watchlist SSE 연결됨");

    disconnectPortfolioSSE();

    watchlistEventSource.onerror = () => {
        console.warn("⚠️ Watchlist SSE 연결 오류, 연결 종료");
        watchlistEventSource.close();
        watchlistEventSource = null;
    };
}


function disconnectWatchlistSSE() {
    if (watchlistEventSource) {
        watchlistEventSource.close();
        watchlistEventSource = null;
        console.log("🔌 Watchlist SSE 연결 해제");
    }

    fetch('/sse/disconnect/watchlist', { method: 'POST' });
}

function disconnectSSEAndLogout(event) {
    event.preventDefault();

    if (portfolioEventSource) {
        disconnectPortfolioSSE();
    }
    if (watchlistEventSource) {
        disconnectWatchlistSSE();
    }

    event.target.form.submit();
}
