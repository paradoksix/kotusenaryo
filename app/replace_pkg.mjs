import fs from 'fs';
import path from 'path';

function walkDir(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? lockWalk(dirPath) : callback(path.join(dir, f));
    });
}
function lockWalk(dir) {
    walkDir(dir, processFile);
}
function processFile(filePath) {
    if (filePath.endsWith('.kt') || filePath.endsWith('.xml') || filePath.endsWith('.kts') || filePath.endsWith('.java')) {
        let content = fs.readFileSync(filePath, 'utf8');
        let newContent = content.replace(/com\.example/g, 'app.kotusenaryo');
        if (content !== newContent) {
            fs.writeFileSync(filePath, newContent, 'utf8');
            console.log('Updated: ' + filePath);
        }
    }
}
lockWalk('./app/');
