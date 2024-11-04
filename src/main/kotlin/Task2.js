const asyncMap = (array, asyncTransform) => {
    return Promise.all(array.map(item => asyncTransform(item)));
};


const testFunc = async (num) => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (num < 0) {
                reject(new Error(`negative value: ${num}`));
            } else if (num % 2 === 0) {
                resolve(num * 2);
            } else {
                resolve(num ** 2);
            }
        }, 1000);
    });
};

const numbers1 = [1, 2, 3, 1, 4];
const numbers2 = [1, 2, 3, -1, 4];

const start = performance.now();

const promise1 = asyncMap(numbers1, testFunc)
    .then((result) => {
        console.log(result);
    })
    .catch((error) => {
        console.error('Error:', error.message);
    });

const promise2 = asyncMap(numbers2, testFunc)
    .then((result) => {
        console.log(result);
    })
    .catch((error) => {
        console.error('Error:', error.message);
    });

Promise.all([promise1, promise2]).then(() => {
    const end = performance.now();
    console.log(`Time: ${end - start} ms`);
});
