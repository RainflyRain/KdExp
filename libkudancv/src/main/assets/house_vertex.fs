precision mediump float;
uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;
uniform mat4 uITNormalMatrix;//法向量MV左上角3x3逆转置矩阵
uniform vec4 uLightPosition;
uniform vec4 uLightColor;
uniform vec3 uEyePosition; //眼睛位置


attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec4 aColor;
attribute vec2 aTextureCoord;
varying vec4 vColor;
varying vec2 vCoordinate;
varying vec3 vNormal;

varying vec3 L;//光源方向
varying vec3 N;//法线方向
varying vec3 Eye;//视线方向

void main() {
  gl_Position = uMVPMatrix*vec4(aPosition,1.0);

  vColor=aColor;
  vCoordinate = aTextureCoord;
  Eye = normalize(uEyePosition - (uMVMatrix*vec4(aPosition,1.0)).xyz);
  //N = normalize((uMVMatrix * vec4(aNormal,0.0)).xyz);//计算模型变换后的法线

  vec4 vn = uITNormalMatrix * vec4(aNormal,0.0);
  N = normalize(vn.xyz);//计算模型变换后的法线
  //N =normalize( gl_NormalMatrix  * aNormal);
  L = normalize(uLightPosition.xyz);//这个是平行光,取光源位置指向原点的方向为光源方向,如果想拿物体做参照物，则物体要停在原点的位置
}