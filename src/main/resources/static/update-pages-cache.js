/**
 * Script pour mettre à jour toutes les pages avec la gestion du cache
 * Ce script lit toutes les pages HTML et ajoute les directives de cache
 */

const fs = require('fs');
const path = require('path');

// Fonction pour mettre à jour un fichier HTML
function updateHTMLFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    let modified = false;
    
    // Vérifier si cache-buster.js est déjà inclus
    if (!content.includes('cache-buster.js')) {
        // Ajouter cache-buster.js juste après <head>
        const headIndex = content.indexOf('<head>');
        if (headIndex !== -1) {
            const insertPosition = content.indexOf('>', headIndex) + 1;
            const cacheScript = `
    <!-- Cache Management -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <script src="js/cache-buster.js"></script>
    `;
            content = content.slice(0, insertPosition) + cacheScript + content.slice(insertPosition);
            modified = true;
            console.log(`✅ Updated: ${path.basename(filePath)}`);
        }
    } else {
        console.log(`⏭️  Already updated: ${path.basename(filePath)}`);
    }
    
    if (modified) {
        fs.writeFileSync(filePath, content, 'utf8');
    }
    
    return modified;
}

// Lister tous les fichiers HTML
const staticDir = __dirname;
const htmlFiles = fs.readdirSync(staticDir)
    .filter(file => file.endsWith('.html'))
    .map(file => path.join(staticDir, file));

console.log(`Found ${htmlFiles.length} HTML files to process...`);

let updatedCount = 0;
htmlFiles.forEach(file => {
    if (updateHTMLFile(file)) {
        updatedCount++;
    }
});

console.log(`\n✅ Process complete! ${updatedCount} files updated.`);
console.log('All pages now have automatic cache management!');
