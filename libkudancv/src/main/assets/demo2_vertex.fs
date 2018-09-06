attribute vec3 aPosition;
uniform mat4 vMatrix;
uniform mat4 uMVPMatrix;
attribute vec4 aColor;
attribute vec2 aTextureCoord;
varying  vec4 vColor;
varying vec2 vCoordinate;

void main() {
  gl_Position = uMVPMatrix*vec4(aPosition,1);
  vColor=aColor;
  vCoordinate = aTextureCoord;
}