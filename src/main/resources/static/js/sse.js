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

//    portfolioEventSource.onmessage = (event) => {
//        const data = JSON.parse(event.data);
//        if (event.lastEventId === 'portfolio-price-update' || event.type === 'portfolio-price-update') {
//            updatePortfolioProfit(data);
//        }
//    };


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

    watchlistEventSource.addEventListener('watchlist-price-update', event => {
        const data = JSON.parse(event.data);
        updateWatchlistItem(data);
    });

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

//function updatePortfolioProfit(updateData) {
//    const card = document.querySelector(`[data-portfolioId="${updateData.portfolioId}"]`);
//    if (!card) return;
//
//    const priceSpan =
//}

function updateWatchlistItem(updateData) {
    const card = document.querySelector(`[data-symbol="${updateData.symbol}"]`);
    if (!card) return;

    const priceSpan = card.querySelector(".price");
    if (priceSpan) {
        priceSpan.textContent = updateData.price.toFixed(2);
    }

    const rateSpan = card.querySelector(".rate");
    if (rateSpan) {
        if (updateData.rate > 0) {
            rateSpan.parentElement.className = "text-danger fw-bold";
            rateSpan.textContent = `${updateData.rate.toFixed(2)}`;
        } else if (updateData.rate < 0) {
            rateSpan.parentElement.className = "text-primary fw-bold";
            rateSpan.textContent = `${Math.abs(updateData.rate).toFixed(2)}`;
        } else {
            rateSpan.parentElement.className = "text-secondary fw-bold";
            rateSpan.textContent = `0.00%`;
        }
    }
}
