precision mediump float;
varying vec4 vColor;
varying vec2 vCoordinate;
uniform sampler2D vTexture;
void main() {
    vec4 textureColor = texture2D(vTexture,vCoordinate);
    vec4 color = vColor + textureColor;
    gl_FragColor =color;
}