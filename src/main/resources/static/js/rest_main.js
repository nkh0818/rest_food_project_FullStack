document.addEventListener('DOMContentLoaded', () => {
    // 현재 URL의 마지막 숫자(ID) 추출
    const pathSegments = window.location.pathname.split('/');
    const id = pathSegments[pathSegments.length - 1];
    
    if (id) {
        fetchRestDetail(id);
    }
});
async function fetchRestDetail(id) {
    try {
        const response = await fetch(`/api/restarea/${id}`);
        const data = await response.json();
        
        document.getElementById('restName').innerText = data.name;
        document.getElementById('restRoute').innerText = data.routeName;
        
        // 매장 리스트 출력 로직...
    } catch (error) {
        console.error("데이터 로드 실패:", error);
    }
}
async function fetchDetail(id) {
    const response = await fetch(`/api/restarea/${id}`);
    const data = await response.json();
    
    document.getElementById('restName').innerText = data.name;
    document.getElementById('restRoute').innerText = data.routeName;
    
    renderShops(data.shops); // 서버에서 준 매장/음식 데이터 렌더링
}

function renderShops(shops) {
    const shopList = document.getElementById('shopList');
    shopList.innerHTML = shops.map(shop => 
        `<div class="list-item" onclick="showMenus(${JSON.stringify(shop.menus)})">${shop.name}</div>`
    ).join('');
}

function showMenus(menus) {
    const menuBoard = document.getElementById('menuBoard');
    menuBoard.innerHTML = menus.map(m => `
        <div class="menu-card">
            <strong>${m.foodName}</strong> - ${m.price}원
        </div>
    `).join('');
}