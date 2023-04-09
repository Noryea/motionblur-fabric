#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float BlendFactor = 0.75;

void main() {
    gl_FragColor = mix(texture2D(DiffuseSampler, texCoord), texture2D(PrevSampler, texCoord), BlendFactor);
    gl_FragColor.w = 1.0;
}
