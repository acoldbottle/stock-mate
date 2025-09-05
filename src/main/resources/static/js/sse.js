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

    holdingEventSource.addEventListener('holding-price-update', event => {
           const data = JSON.parse(event.data);
           updateHolding(data);
       });

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

    portfolioEventSource.addEventListener('portfolio-price-update', event => {
        const data = JSON.parse(event.data);
        updatePortfolioProfit(data);
    });

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

function updateHolding(updateData) {
    const holdingTarget = document.querySelector(`[data-symbol="${updateData.symbol}"]`);
    if (!holdingTarget) return;

    const currentPriceSpan = holdingTarget.querySelector(".currentPrice");
    if (currentPriceSpan) {
        currentPriceSpan.textContent = updateData.price.toFixed(2);
    }
    const rateSpan = holdingTarget.querySelector(".rate");
    if (rateSpan) {
        if (updateData.rate > 0) {
            rateSpan.parentElement.className = "text-danger positive";
            rateSpan.textContent = "+" + updateData.rate.toFixed(2);
        } else if (updateData.rate < 0) {
            rateSpan.parentElement.className = "text-primary negative";
            rateSpan.textContent = updateData.rate.toFixed(2);
        } else {
            rateSpan.parentElement.className = "text-secondary zero";
            rateSpan.textContent = `0.00`;
        }
    }
    const quantitySpan = holdingTarget.querySelector('[data-quantity]');
    const quantity = Number(quantitySpan.dataset.quantity);
    const totalAmount = Number(updateData.price) * quantity;
    const totalAmountSpan = holdingTarget.querySelector(".totalAmount");
    if(totalAmountSpan){
        totalAmountSpan.textContent = totalAmount.toFixed(2);
    }

    const purchasePriceSpan = holdingTarget.querySelector('[data-purchase-price]');
    const purchasePrice = Number(purchasePriceSpan.dataset.purchasePrice);
    const profitAmount = totalAmount - purchasePrice * quantity;
    const profitRate = (profitAmount / (purchasePrice * quantity)) * 100;
    const profitAmountSpan = holdingTarget.querySelector(".profitAmount");
    const profitRateSpan = holdingTarget.querySelector(".profitRate");
    if (profitAmount > 0) {
        profitAmountSpan.parentElement.className = "text-danger";
        profitAmountSpan.textContent = "+$" + profitAmount.toFixed(2);
        profitRateSpan.textContent = "+" + profitRate.toFixed(2);
    } else if (profitAmount < 0) {
        profitAmountSpan.parentElement.className = "text-primary";
        profitAmountSpan.textContent = "-$" + Math.abs(profitAmount).toFixed(2);
        profitRateSpan.textContent = profitRate.toFixed(2);
    } else {
        profitAmountSpan.parentElement.className = "text-secondary";
        profitAmountSpan.textContent = "$" + `0.00`;
        profitRateSpan.textContent = `0.00`;
    }

    const oldHoldingTotalAmountSpan = holdingTarget.querySelector('[data-total-amount]');
    const oldHoldingTotalAmount = Number(oldHoldingTotalAmountSpan.dataset.totalAmount);

    const oldHoldingProfitAmountSpan = holdingTarget.querySelector('[data-profit-amount]');
    const oldHoldingProfitAmount = Number(oldHoldingProfitAmountSpan.dataset.profitAmount);

    const portfolioTarget = document.querySelector(`[data-portfolioId="${updateData.portfolioId}"]`);
    const oldPortfolioProfitAmountSpan = portfolioTarget.querySelector('[data-portfolio-profit-amount]')
    const oldPortfolioProfitAmount = Number(oldPortfolioProfitAmountSpan.dataset.portfolioProfitAmount);
    const oldPortfolioCurrentValueSpan = portfolioTarget.querySelector('[data-portfolio-current-value]')
    const oldPortfolioCurrentValue = Number(oldPortfolioCurrentValueSpan.dataset.portfolioCurrentValue);

    const portfolioCurrentValue = oldPortfolioCurrentValue - oldHoldingTotalAmount + totalAmount;
    const portfolioProfitAmount = oldPortfolioProfitAmount - oldHoldingProfitAmount + profitAmount;
    const purchaseAmount = portfolioCurrentValue - portfolioProfitAmount;
    const portfolioProfitRate = (portfolioProfitAmount / purchaseAmount) * 100;

    const portfolioCurrentValueSpan = portfolioTarget.querySelector(".portfolio-current-value");
    if (portfolioCurrentValueSpan) {
        portfolioCurrentValueSpan.textContent = portfolioCurrentValue.toFixed(2);
    }

    const portfolioProfitAmountSpan = portfolioTarget.querySelector(".portfolio-profit-amount");
    const portfolioProfitRateSpan = portfolioTarget.querySelector(".portfolio-profit-rate");
    if (portfolioProfitAmountSpan) {
        if (portfolioProfitAmount > 0) {
            portfolioProfitAmountSpan.parentElement.className = "text-danger fw-bold";
            portfolioProfitAmountSpan.textContent = "+$" + portfolioProfitAmount.toFixed(2);
            portfolioProfitRateSpan.textContent = "+" + portfolioProfitRate.toFixed(2);
        } else if (portfolioProfitAmount < 0) {
            portfolioProfitAmountSpan.parentElement.className = "text-primary fw-bold";
            portfolioProfitAmountSpan.textContent = Math.abs(portfolioProfitAmount).toFixed(2);
            portfolioProfitRateSpan.textContent = Math.abs(portfolioProfitRate).toFixed(2);
        } else {
            portfolioProfitAmountSpan.parentElement.className = "text-secondary fw-bold";
            portfolioProfitAmountSpan.textContent = "+$" + `0.00`;
            portfolioProfitRateSpan.textContent = `0.00`;
        }
    }
}


function updatePortfolioProfit(updateData) {
    const card = document.querySelector(`[data-portfolioId="${updateData.portfolioId}"]`);
    if (!card) return;

    const currentValue = card.querySelector(".current-value");
    if(currentValue) {
        currentValue.textContent = updateData.portfolioCurrentValue.toFixed(2);
    }

    const profitAmount = card.querySelector(".portfolio-profit-amount");
    const profitRate = card.querySelector(".portfolio-profit-rate");

    if (profitAmount) {
        if (updateData.portfolioProfitAmount > 0) {
            profitAmount.parentElement.className = "text-danger fw-bold";
            profitAmount.textContent = "+$" + updateData.portfolioProfitAmount.toFixed(2);
            profitRate.textContent = "+" + updateData.portfolioProfitRate.toFixed(2);
        } else if (updateData.portfolioProfitAmount < 0) {
            profitAmount.parentElement.className = "text-primary fw-bold";
            profitAmount.textContent = "-$" + Math.abs(updateData.portfolioProfitAmount).toFixed(2);
            profitRate.textContent = updateData.portfolioProfitRate.toFixed(2);
        } else {
            profitAmount.textContent = `0.00`;
            profitRate.textContent = `0.00%`;
        }
    }
}

function updateWatchlistItem(updateData) {
    const card = document.querySelector(`[data-symbol="${updateData.symbol}"]`);
    if (!card) return;

    const priceSpan = card.querySelector(".price");
    if (priceSpan) {
        priceSpan.textContent = "$" + updateData.price.toFixed(2);
    }

    const rateSpan = card.querySelector(".rate");
    if (rateSpan) {
        if (updateData.rate > 0) {
            rateSpan.parentElement.className = "text-danger fw-bold";
            rateSpan.textContent = "+" + updateData.rate.toFixed(2);
        } else if (updateData.rate < 0) {
            rateSpan.parentElement.className = "text-primary fw-bold";
            rateSpan.textContent = updateData.rate.toFixed(2);
        } else {
            rateSpan.parentElement.className = "text-secondary fw-bold";
            rateSpan.textContent = `0.00`;
        }
    }
}
