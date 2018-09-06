precision mediump float;
varying vec4 vColor;
uniform vec4 uLightColor;
varying vec2 vCoordinate;
uniform sampler2D vTexture;
void main() {
   // vec4 textureColor = texture2D(vTexture,vCoordinate);
    gl_FragColor =uLightColor ;
}