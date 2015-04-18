/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//Query 2
$(".addField").change(function () {
    var type = $(this).find("option:selected").val();
    if (type !== undefined) {
        switch (type) {
            case "1":
                $(".hiddenField1").show();
                $(".hiddenField2").hide();
                break;
            case "2":
                $(".hiddenField2").show();
                $(".hiddenField1").hide();
                break;
            case "":
                $(".hiddenField2").hide();
                $(".hiddenField1").hide();
        }
    }
});

//Query 4
$(".getComputerList").change(function () {
    var customerName = $(this).find("option:selected").val();
    if (customerName !== undefined) {
        $.get("./service?query=4&buyer=" + customerName, function (data) {
            $(".computerList").html(data);
            $(".computerList").fadeIn("slow");
        });
    }
});

//Query 5
$(".query5Form").submit(function (e) {
    var customerName = $(".customerName").find("option:selected").val();
    var year = $(".year").val();

    if (customerName !== undefined && year !== undefined) {
        $.get("./service?query=5&buyer=" + customerName + "&year=" + year, function (data) {
            $(".computerList").fadeOut("fast", function () {
                $(".computerList").html(data);
                $(".computerList").fadeIn("slow");
            });

        });
    }
    e.preventDefault();
});

//Query 6
$(".query6Form").submit(function (e) {
    var manufacturer = $(".manufacturer").find("option:selected").val();

    if (manufacturer !== undefined) {
        $.get("./service?query=6&manufacturer=" + manufacturer, function (data) {
            $(".customerTable").fadeOut("fast", function () {
                $(".customerTable").html(data);
                $(".customerTable").fadeIn("slow");
            });

        });
    }
    e.preventDefault();
});

//Query 7
$(".query7Form").submit(function (e) {
    var os = $(".os").val();

    if (os !== undefined) {
        $.get("./service?engine=bm25&query=" + os, function (data) {
            $(".customerTable").fadeOut("fast", function () {
                $(".customerTable").html(data);
                $(".customerTable").fadeIn("slow");
            });

        });
    }
    e.preventDefault();
});

//Query 8
$(".query8Form").submit(function (e) {
    var price = $(".price").val();

    if (price !== undefined) {
        $.get("./service?query=8&price=" + price, function (data) {
            $(".customerTable").fadeOut("fast", function () {
                $(".customerTable").html(data);
                $(".customerTable").fadeIn("slow");
            });

        });
    }

    e.preventDefault();
});

//Query 9
$(".query9Form").submit(function (e) {
    var serialNo = $(".serialNo").val();

    if (serialNo !== undefined) {
        $.get("./service?query=9&serialNo=" + serialNo, function (data) {
            $(".computerTable").fadeOut("fast", function () {
                $(".computerTable").html(data);
                $(".computerTable").fadeIn("slow");
            });

        });
    }

    e.preventDefault();
});

//Query 10
$(".query10Form").submit(function (e) {
    var year = $(".year").val();

    if (year !== undefined) {
        $.get("./service?query=10&year=" + year, function (data) {
            $(".customerTable").fadeOut("fast", function () {
                $(".customerTable").html(data);
                $(".customerTable").fadeIn("slow");
            });

        });
    }

    e.preventDefault();
});