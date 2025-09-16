#!/bin/bash
set -e

APP_NAME="JRelay"
VERSION="0.0.2"
MAINTAINER="Gian <email@ejemplo.com>"
DESCRIPTION="Aplicación Swing convertida a .deb con icono y lanzador"

JAR_FILE="/home/gian/Escritorio/Dev/JavaProjects/curlup/out/artifacts/jrelay_desktop_jar/jrelay-desktop.jar"
ICON_FILE="/home/gian/Escritorio/Dev/JavaProjects/curlup/src/main/resources/img/jrelay.png"

PKG_DIR="${APP_NAME}"

rm -rf "$PKG_DIR"
mkdir -p "$PKG_DIR/DEBIAN"
mkdir -p "$PKG_DIR/usr/bin"
mkdir -p "$PKG_DIR/usr/share/java"
mkdir -p "$PKG_DIR/usr/share/applications"
mkdir -p "$PKG_DIR/usr/share/icons/hicolor/64x64/apps"

cat > "$PKG_DIR/DEBIAN/control" <<EOF
Package: $APP_NAME
Version: $VERSION
Section: utils
Priority: optional
Architecture: all
Depends: default-jre
Maintainer: $MAINTAINER
Description: $DESCRIPTION
EOF

cat > "$PKG_DIR/usr/bin/$APP_NAME" <<EOF
#!/bin/bash
exec java -jar /usr/share/java/jrelay-desktop.jar "\$@"
EOF
chmod 755 "$PKG_DIR/usr/bin/$APP_NAME"

cp "$JAR_FILE" "$PKG_DIR/usr/share/java/"
cp "$ICON_FILE" "$PKG_DIR/usr/share/icons/hicolor/64x64/apps/$APP_NAME.png"

cat > "$PKG_DIR/usr/share/applications/$APP_NAME.desktop" <<EOF
[Desktop Entry]
Name=$APP_NAME
Comment=$DESCRIPTION
Exec=$APP_NAME
Icon=$APP_NAME
Terminal=false
Type=Application
Categories=Utility;
EOF

dpkg-deb --build "$PKG_DIR"

echo "✅ Paquete creado: ${PKG_DIR}.deb"
echo "Instala con: sudo dpkg -i ${PKG_DIR}.deb"
