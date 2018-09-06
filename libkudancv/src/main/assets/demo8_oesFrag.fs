
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec4 vColor;
varying vec2 vCoordinate;
uniform sampler2D vTexture;
uniform samplerExternalOES oesTexture;
void main() {
   //vec4 textureColor = texture2D(vTexture,vCoordinate);
   vec4 textureColor =  texture2D(oesTexture, vCoordinate);
   gl_FragColor =textureColor  ;
   //gl_FragColor =vColor + textureColor;
    //gl_FragColor =vColor  ;
}

		 