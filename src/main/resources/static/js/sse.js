let holdingEventSource;
let portfolioEventSource;
let watchlistEventSource;

function connectHoldingSSE(portfolioId) {
    if(!portfolioId){
        return;
    }
    if (holdingEventSource && holdingEventSource.readyState !== holdingEventSource.CLOSED) {
        return;
    }

    holdingEventSource = new EventSource(`/sse/connect/holding/${portfolioId}`);

    disconnectPortfolioSSE();

    holdingEventSource.onerror = () => {
            holdingEventSource.close();
            holdingEventSource = null;
        };
}

function disconnectHoldingSSE() {
    if (holdingEventSource) {
        holdingEventSource.close();
        holdingEventSource = null;
    }

    fetch(`/sse/disconnect/holding/${portfolioId}`, { method: 'POST' })
}

function connectPortfolioSSE() {
    if (portfolioEventSource && portfolioEventSource.readyState !== portfolioEventSource.CLOSED) {
        return;
    }
    portfolioEventSource = new EventSource("/sse/connect/portfolio");

    disconnectHoldingSSE();
    disconnectWatchlistSSE();

    portfolioEventSource.onerror = () => {
        portfolioEventSource.close();
        portfolioEventSource = null;
    };
}


function disconnectPortfolioSSE() {
    if (portfolioEventSource) {
        portfolioEventSource.close();
        portfolioEventSource = null;
    }

    fetch('/sse/disconnect/portfolio', { method: 'POST' });
}

function connectWatchlistSSE() {
    if (watchlistEventSource && watchlistEventSource.readyState !== watchlistEventSource.CLOSED) {
        return;
    }
    watchlistEventSource = new EventSource("/sse/connect/watchlist");

    disconnectPortfolioSSE();
    disconnectHoldingSSE();

    watchlistEventSource.onerror = () => {
        watchlistEventSource.close();
        watchlistEventSource = null;
    };
}


function disconnectWatchlistSSE() {
    if (watchlistEventSource) {
        watchlistEventSource.close();
        watchlistEventSource = null;
    }

    fetch('/sse/disconnect/watchlist', { method: 'POST' });
}

function disconnectSSEAndLogout(event) {
    event.preventDefault();

    if (holdingEventSource) {
        disconnectHoldingSSE();
    }
    if (portfolioEventSource) {
        disconnectPortfolioSSE();
    }
    if (watchlistEventSource) {
        disconnectWatchlistSSE();
    }

    event.target.form.submit();
}
