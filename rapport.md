## Etape 0
[Github dai-http-infra](https://github.com/nathanrayburn/dai-lab-http-infrastructure)
## Etape 1

- Créer le fichier docker
- Créer le fichier conf nginx

Build le container
```
docker build -t nginx .
```
Run le container

```
docker run -p 8080:80 nginx
```

### Tailwind CSS

#### Install Tailwind css

```
npm install -D tailwindcss
npx tailwindcss init
```

#### Configurataion of the template

`tailwind.config.js`

```
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js}"],
  theme: {
    extend: {},
  },
  plugins: [],
}
```
#### Add Tailwind directives to css

`src/input.css`

```
@tailwind base;
@tailwind components;
@tailwind utilities;
```

#### Start the Tailwind CLI buil

```
npx tailwindcss -i ./src/input.css -o ./dist/output.css --watch
```

