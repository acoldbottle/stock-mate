let watchlistEventSource;

function connectWatchlistSSE() {
    if (watchlistEventSource && watchlistEventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    watchlistEventSource = new EventSource("/sse/connect/watchlist");
    console.log("✅ Watchlist SSE 연결됨");
    watchlistEventSource.onerror = () => {
        console.warn("⚠️ Watchlist SSE 연결 오류, 연결 종료");
        watchlistEventSource.close();
    };
}


function disconnectWatchlistSSE() {
    if (watchlistEventSource) {
        watchlistEventSource.close();
        console.log("🔌 Watchlist SSE 연결 해제");
    }

    fetch('/sse/disconnect/watchlist', { method: 'POST' });
}

function disconnectSSEAndLogout(event) {
    event.preventDefault();
    disconnectWatchlistSSE();
    event.target.form.submit();
}
