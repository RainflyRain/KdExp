attribute vec4 aPosition;
uniform mat4 vMatrix;
uniform mat4 uMVPMatrix;
varying  vec4 vColor;

void main() {
  gl_Position = uMVPMatrix*aPosition;
}