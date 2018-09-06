precision mediump float;
uniform sampler2D uTexture;
uniform vec4 uAmbient;		//用于传递给片元着色器的环境光变量
uniform vec4 uDiffuse;		//用于传递给片元着色器的漫反射光变量
uniform vec4 uSpecular;	    //用于传递给片元着色器的镜面光变量

uniform float uHasTexture; //是否有纹理传入的标识位
uniform float uHasLighting;//有光源传入的标识位
uniform vec4 uLightColor;

varying vec4 vColor;
varying vec2 vCoordinate;
varying vec3 L;//光源方向
varying vec3 N;//法线方向
varying vec3 Eye;//看向某个顶点的视线方向

float specular(vec3 N,vec3 L ,vec3 Eye)
{
    vec3 R = normalize(2.0*dot(N,L)*N - L  );//反射光线
    //vec3 R = normalize(reflect(-L,N));
	float percent =  pow(max(dot( Eye,R),0.0) ,30.0);//这里的数字是衰减因子，数值越大，高光区越小
	return  percent ;
}
//环境光
float ambient( )
{
    return 0.3;
}
//漫反射
float diffuse(vec3 N,vec3 L)
{
    float percent =  (dot(N,L)+1.0)/2.0;//改进的半lamert模型
    //float percent =  max(dot(N,L),0.0) ;//基础漫反射
    return  percent ;
}
void main() {
    vec4 color ;
   // Eye = normalize(Eye);
   // N = normalize(N) ;
   // L = normalize(L);
   if(uHasLighting == 1.0){//有光源
        if(uHasTexture == 1.0){//有纹理

            vec4 textureColor = texture2D(uTexture,vCoordinate);

            vec4 ambientValue =   textureColor * uLightColor * uAmbient * ambient();
            vec4 diffuseValue =   textureColor * uLightColor * uDiffuse * diffuse(N,L) ;
            vec4 specularValue =   uLightColor * uSpecular * specular(N,L,Eye);

            color =  ambientValue;

        }else{
            //变量名和方法名不能同名，不然什么东西都不会显示，这应该是glsl的bug

            vec4 ambientValue =    uLightColor * uAmbient * ambient();
            vec4 diffuseValue =    uLightColor * uDiffuse * diffuse(N,L) ;
            vec4 specularValue =   uLightColor * uSpecular * specular(N,L,Eye);
            color =   ambientValue+diffuseValue+  specularValue;

        }
   }else{

        if(uHasTexture == 1.0){//有纹理

               vec4 textureColor = texture2D(uTexture,vCoordinate);
               vec4 ambientValue =    textureColor * uAmbient * ambient();
               color =  ambientValue;

        }else{
               //变量名和方法名不能同名，不然什么东西都不会显示，这应该是glsl的bug

               vec4 ambientValue =     uAmbient * ambient();
               color =   ambientValue ;

        }

   }
    gl_FragColor = color;
}
/**
 * 镜面反射，假设模型如下图所示称为Blin-Phong模型的光照模型,以下是简化版，完整版是什么呢，暂时没去看
 *
 * L是入射光线(从顶点指向光源) R是反射光线 ，N是该处的法线
 *
 * Blin-Phong模型认为光照 模型镜面反射成分主要与     反射光线与视线夹角的余弦   相关，如下图a角的余弦
 *
 *  设 R点指向L点的位置 向量为2P
 *
 *   R = L-2P
 *
 *   P  = L - s
 *
 *   因为 s = (L.N)* N/(|L|*|N|) //请谨慎推导
 *
 *   所以 R = 2*(L.N)*N - L
 *
 *  cos(a) = (R.Eye) / (|R|*|Eye|) ;//
 *  因为都是单位向量
 *  所以cos(a) = R.Eye;
 *
 *
 * 			R         N    			L
 * 			\——p      |——p 		/
 * 			 \        |s  	   /
 * 			  \	      |  	  /
 * 			   \      |thita /
 * 				\     |     /
 * 				 \	  |    /
 * 				  \   |   /
 * 				   \  |  /
 * 					\ | /
 * 				a/   \|/
 * Eye—————————————————
 *
 */
