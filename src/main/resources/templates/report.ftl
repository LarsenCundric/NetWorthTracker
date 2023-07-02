<!DOCTYPE html> <html>

<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://www.gstatic.com/charts/loader.js"></script>
</head>

<body class="container mx-auto bg-gray-50 pb-16">
    <h1 class="mt-8 mb-8 text-2xl text-blue-800">${title}</h1>
    <div class="bg-white shadow-2xl p-8 rounded-xl">
        <table class="w-full">
            <thead>
                <tr class="text-blue-800 border-b-4 border-blue-800">
                    <!-- Empty cell over two columns -->
                    <th colspan="2">&nbsp;</th>
                    <!-- Loop over the snapshots -->
                    <#list snapshots as snapshot>
                        <th class="text-right">
                            <!-- Formatted snapshot’s date -->
                            ${snapshot.date?date("yyyy-MM-dd")?string.medium}
                        </th>
                    </#list>
                </tr>
            </thead>
            <tbody>
                <#list book.institutions as institution>
                    <tr class="border-y font-bold">
                        <td colspan="${snapshots?size + 2}" class="pt-4">${institution.name}</td>
                    </tr>
                    <#list book.accountsInInstitution(institution) as account>
                        <tr>
                            <td class="w-8 text-center">${account.metadata.riskLevel.symbol}</td>
                            <td>${account.name}</td>
                            <#list snapshots as snapshot>
                                <td class="text-right tabular-nums">
                                    <#if snapshot.accountBalance(account)??>
                                        ${snapshot.accountBalance(account).toValue(book.mainCurrency, snapshot.date)?round}
                                    <#else>
                                        -
                                    </#if>
                                </td>
                            </#list>
                        </tr>
                    </#list>
                </#list>
            </tbody>
            <tfoot>
                <!-- This row is here for styling concerns only -->
                <tr class="pt-4 border-t"><td>&nbsp;</td></tr>
                <tr class="border-t-4 text-blue-800 border-blue-800 font-bold">
                    <td colspan="2">Total</td>
                    <#list snapshots as snapshot>
                        <td class="text-right tabular-nums"> ${snapshotTotals[snapshot.date]?round}</td>
                    </#list>
                </tr>
            </tfoot>
        </table>
    </div>

    <div class="w-full bg-white rounded-xl shadow-2xl mt-8 overflow-hidden">
        <h2 class="px-8 pt-8 text-2xl text-blue-800">By account</h2>
        <div id="chart_by_account" style="height: 400px">
            <#-- chart by account display -->
        </div>
    </div>
</body>

<script>
    google.charts.load('current', { packages: ['corechart'] });
    google.charts.setOnLoadCallback(drawChartByAccount);

    function drawChartByAccount() {
        const chartData = JSON.parse('${chartDataByAccountJson?no_esc}');

        chartData.forEach((row, i) => {
            row[0] = i === 0 ? row[0] : new Date(row[0])
        })

        const data = google.visualization.arrayToDataTable(chartData);
        const formatter = new google.visualization.NumberFormat({ fractionDigits: 0 });

        chartData[0].forEach((_, i) => i !== 0 && formatter.format(data, i))

        const options = {
            hAxis: { format: 'MMM d, y' }, // Date format
            vAxis: { minValue: 0 }, // Min value on the vertical axis
            seriesType: 'bars',
            isStacked: true,
            // Additional series for the totals (the first column doesn’t count in the
            // indices, hence the length minus 2 and not minus 1)
            series: { [chartData[0].length - 2]: { type: 'line' } }
        };

        new google.visualization
            .ComboChart(document.getElementById('chart_by_account'))
            .draw(data, options);
    }
</script>

</html>
