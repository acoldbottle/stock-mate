export function initHoldingChart(holdingList) {
    const ctx = document.getElementById('holdingChart')?.getContext('2d');
    if (!ctx) return;

    const totalSum = holdingList.reduce((sum, h) => sum + Number(h.totalAmount), 0);

    let labels = [];
    let data = [];
    let backgroundColors = [];

    if (totalSum === 0) {
        labels = [''];
        data = [1];
        backgroundColors = ['#CCCCCC'];
    } else if (holdingList.length > 10) {
        const sortedHoldings = holdingList.slice().sort((a, b) => b.totalAmount - a.totalAmount);
        const top9 = sortedHoldings.slice(0, 9);
        const etc = sortedHoldings.slice(9);
        const etcSum = etc.reduce((sum, h) => sum + Number(h.totalAmount), 0);

        top9.forEach(h => {
            const percent = (h.totalAmount / totalSum * 100).toFixed(1);
            labels.push(`${h.symbol} (${percent}%)`);
            data.push(Number(h.totalAmount));
        });

        const etcPercent = (etcSum / totalSum * 100).toFixed(1);
        labels.push(`ETC (${etcPercent}%)`);
        data.push(etcSum);

        backgroundColors = [
            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
            '#FF9F40', '#FFCD56', '#C9CBCF', '#8AC926', '#1982C4', '#AAAAAA'
        ];
    } else {
        holdingList.forEach(h => {
            const percent = (h.totalAmount / totalSum * 100).toFixed(1);
            labels.push(`${h.symbol} (${percent}%)`);
            data.push(Number(h.totalAmount));
        });

        backgroundColors = [
            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
            '#FF9F40', '#FFCD56', '#C9CBCF', '#8AC926', '#1982C4'
        ].slice(0, holdingList.length);
    }

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'right' },
                tooltip: { enabled: true }
            }
        }
    });
}