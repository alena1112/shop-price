function parseMessage(response) {
    let text = JSON.parse(response);
    let element = document.getElementById("infoMessage");
    element.innerHTML = "Response from server: "
    if (text.error) {
        element.innerHTML += text.error;
        element.style.color = 'red';
    } else {
        element.innerHTML += text.message;
        element.style.color = 'green';
    }
    return text;
}

function loadShops() {
    fetch("http://localhost:8080/shop").then(function (response) {
        response.text().then(function (text) {
            let json = parseMessage(text).data;
            let selectForLoading = document.getElementById("shopSelect");
            let selectForFiltering = document.getElementById("shopFilter");
            addShopOptionsInSelect(selectForLoading, json);
            addShopOptionsInSelect(selectForFiltering, json);
        })
    });
}

function getAllMaterials(isUpdateOrderFilter) {
    let shopFilter = document.getElementById('shopFilter');
    let orderFilter = document.getElementById('materialOrderFilter');
    let nameFilter = document.getElementById('nameFilter');

    let params = "?shop=" + (shopFilter !== null && shopFilter.value !== 'ALL' ? shopFilter.value : "");
    params += "&order=" + (orderFilter !== null && orderFilter.value !== 'ALL' ? orderFilter.value : "");
    params += "&material=" + (nameFilter !== null ? nameFilter.value : "");
    fetch("http://localhost:8080/material" + params)
        .then(function (response) {
            response.text().then(function (text) {
                render(text);
            })
        });

    if (isUpdateOrderFilter) {
        getOrders();
    }
}

function getOrders() {
    let shopFilter = document.getElementById('shopFilter');
    let shopParam = shopFilter !== null && shopFilter.value !== 'ALL' ? "?shop=" + shopFilter.value : "";
    fetch("http://localhost:8080/material/orders" + shopParam)
        .then(function (response) {
            response.text().then(function (text) {
                let json = parseMessage(text).data;
                let orderFilter = document.getElementById("materialOrderFilter");
                while (orderFilter.hasChildNodes()) {
                    orderFilter.removeChild(orderFilter.lastChild);
                }

                let optionElement = document.createElement("option");
                optionElement.innerHTML = 'ALL';
                orderFilter.appendChild(optionElement);
                for (let i = 0; i < json.length; i++) {
                    optionElement = document.createElement("option");
                    optionElement.value = json[i];
                    optionElement.innerHTML = json[i];
                    orderFilter.appendChild(optionElement);
                }
            })
        });
}

function render(value) {
    let json = parseMessage(value).data;
    let table = document.getElementById("materialsBody");
    clearTable("materialsBody")

    for (let i = 0; i < json.length; i++) {
        let material = json[i];

        let newRow = table.insertRow();
        let img = document.createElement("img");
        img.src = material.imageURL;
        newRow.insertCell().appendChild(img);

        newRow.insertCell().appendChild(document.createTextNode(material.name));
        newRow.insertCell().appendChild(document.createTextNode(material.shop));
        newRow.insertCell().appendChild(document.createTextNode(material.price));
        newRow.insertCell().appendChild(document.createTextNode(material.number));
        newRow.insertCell().appendChild(document.createTextNode(material.unitPriceWithDelivery));

        let editButton = document.createElement("button");
        editButton.innerHTML = "Edit";
        editButton.onclick = function () {
            let materialDialog = document.getElementById('materialDialog');
            materialDialog.showModal();

            document.getElementById('id').value = material.id;

            let imageUrl = document.getElementById('imageUrl');
            imageUrl.value = material.imageURL;
            imageUrl.setAttribute('disabled', '');

            let name = document.getElementById('name');
            name.value = material.name;
            name.setAttribute('disabled', '');

            let price = document.getElementById('price');
            price.value = material.price;
            price.setAttribute('disabled', '');

            document.getElementById('number').value = material.number;

            let unit = document.getElementById('unitPriceWithDelivery');
            unit.value = material.unitPriceWithDelivery;
            unit.setAttribute('disabled', '')
        }
        newRow.insertCell().appendChild(editButton);

        let addButton = document.createElement("button");
        addButton.innerHTML = "Add";
        addButton.value = material.id;
        addButton.onclick = function () {
            fetch("http://localhost:8080/material/" + this.value)
                .then(function (response) {
                    response.text().then(function (text) {
                        let material = parseMessage(text).data;
                        addMaterialInJewelryMaterialsTable(material, "1")
                        updateOriginalPrice();
                    })
                });
        }
        newRow.insertCell().appendChild(addButton);
    }
}

function addMaterialInJewelryMaterialsTable(material, count) {
    let jewelryTable = document.getElementById("jewelryMaterials");
    let jewelryRow = jewelryTable.insertRow();
    jewelryRow.id = material.id

    let img = document.createElement("img");
    img.src = material.imageURL;
    jewelryRow.insertCell().appendChild(img);

    jewelryRow.insertCell().appendChild(document.createTextNode(material.name));

    let inputCount = document.createElement("input");
    inputCount.type = "text";
    inputCount.value = count;
    inputCount.onchange = function () {
        updateOriginalPrice();
    }
    jewelryRow.insertCell().appendChild(inputCount);

    let deleteButton = document.createElement("button");
    deleteButton.innerHTML = "Delete";
    deleteButton.onclick = function () {
        let p = this.parentNode.parentNode;
        p.parentNode.removeChild(p);
        updateOriginalPrice();
    }
    jewelryRow.insertCell().appendChild(deleteButton);
}

function loadMaterials() {
    fetch('http://localhost:8080/material/load', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    }).then(function (response) {
        window.location.reload();//TODO удалить
        response.text().then(function (text) {
            parseMessage(text);
        });
    });
}

function loadOrderPage() {
    const pageTextArea = document.getElementById('pageTextArea');
    const shopSelect = document.getElementById('shopSelect');
    fetch('http://localhost:8080/material/loadPage', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            shop: shopSelect.value,
            text: pageTextArea.value
        })
    }).then(function (response) {
        window.location.reload();//TODO удалить
        response.text().then(function (text) {
            parseMessage(text);
        });
    });
}

function createMaterial() {
    fetch('http://localhost:8080/material', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    }).then(function (response) {
        window.location.reload();//TODO удалить
        response.text().then(function (text) {
            parseMessage(text);
        });
    });
}

function addShopOptionsInSelect(select, shops) {
    for (let i = 0; i < shops.length; i++) {
        let optionElement = document.createElement("option");
        optionElement.value = shops[i].value;
        optionElement.innerHTML = shops[i].name;
        select.appendChild(optionElement);
    }
}

function updateOriginalPrice() {
    let array = readMaterialsTable();
    let price = document.getElementById('originalPrice');
    if (array.length > 0) {
        fetch('http://localhost:8080/material/calculate', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                materials: array
            })
        }).then(function (response) {
            response.text().then(function (text) {
                price.innerHTML = parseMessage(text).data;
            });
        });
    } else {
        price.innerHTML = "";
    }
}

function saveMaterial() {
    let id = document.getElementById('id').value;
    let imageUrl = document.getElementById('imageUrl').value;
    let name = document.getElementById('name').value;
    let price = document.getElementById('price').value;
    let number = document.getElementById('number').value;
    fetch('http://localhost:8080/material', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            id: id,
            imageURL: imageUrl,
            name: name,
            price: price,
            number: number
        })
    }).then(function (response) {
        window.location.reload();//TODO удалить
        response.text().then(function (text) {
            parseMessage(text);
        });
    });
}

function closeMaterialDialog() {
    let materialDialog = document.getElementById('materialDialog');
    materialDialog.close();
}

function openOrderPageDialog() {
    let orderPageDialog = document.getElementById('orderPageDialog');
    orderPageDialog.showModal();
}

function closeOrderPageDialog() {
    let orderPageDialog = document.getElementById('orderPageDialog');
    orderPageDialog.close();
}

function loadJewelries() {
    fetch("http://localhost:8080/jewelry/all")
        .then(function (response) {
            response.text().then(function (text) {
                let jewelries = parseMessage(text).data;
                let jewelryList = document.getElementById("jewelryList");
                for (let i = 0; i < jewelries.length; i++) {
                    let optionElement = document.createElement("option");
                    optionElement.value = jewelries[i].id;
                    optionElement.innerHTML = jewelries[i].name + " - " + jewelries[i].description;
                    jewelryList.appendChild(optionElement);
                }
            })
        });
}

function showJewelry() {
    let jewelryList = document.getElementById("jewelryList");
    let jewelryImg = document.getElementById("jewelryImg");
    let marketPrice = document.getElementById("marketPrice");
    if (jewelryList.value) {
        fetch("http://localhost:8080/jewelry/" + jewelryList.value)
            .then(function (response) {
                response.text().then(function (text) {
                    let json = parseMessage(text).data;
                    jewelryImg.src = json.imageUrl;
                    marketPrice.innerHTML = json.price;

                    clearTable("jewelryMaterials");

                    for (let i = 0; i < json.materials.length; i++) {
                        let material = json.materials[i];
                        addMaterialInJewelryMaterialsTable(material, material.count)
                    }
                    updateOriginalPrice();
                })
            });
    } else {
        jewelryImg.src = "no_image.png";
        marketPrice.innerHTML = "";
        clearTable("jewelryMaterials");
        let originalPrice = document.getElementById('originalPrice');
        originalPrice.innerHTML = "";
    }
}

function clearTable(tableName) {
    let table = document.getElementById(tableName);
    while (table.hasChildNodes()) {
        table.removeChild(table.lastChild);
    }
}

function saveJewelry() {
    let jewelryId = document.getElementById("jewelryList").value;
    let array = readMaterialsTable();
    if (jewelryId) {
        fetch('http://localhost:8080/jewelry', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                jewelryId: jewelryId,
                materials: array
            })
        }).then(function (response) {
            response.text().then(function (text) {
                parseMessage(text)
            });
        });
    }
}

function readMaterialsTable() {
    let materialsTable = document.getElementById('jewelryMaterials');
    let rows = materialsTable.rows;
    let array = [];
    for (let i = 0; i < rows.length; i++) {
        let obj = {};
        obj.id = rows[i].id;
        obj.count = rows[i].children[2].children[0].value;
        array.push(obj);
    }
    return array;
}