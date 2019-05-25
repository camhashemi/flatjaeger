const axios = require("axios");

async function putSearchRequest(request) {
    return axios.put(
        "https://api.flatjaeger.com/search/requests",
        JSON.stringify(request),
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    )
}

const request = {
    "email": "camhashemi@gmail.com",
    "maxPrice": 2000,
    "minRooms": 2,
    "availableAfter": "2020-06-01",
    "availableBefore": "2020-07-01"
};

putSearchRequest(request)
    .then(response => console.info(response.statusText))
    .catch(err => console.error(err.response));
