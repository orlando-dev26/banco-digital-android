import requests

with open("test_img.jpg", "wb") as f:
    f.write(b"fake image data")

files = {
    'imagen': ('test_img.jpg', open('test_img.jpg', 'rb'), 'image/jpeg')
}
data = {
    'nombre_esperado': 'ORLANDO',
    'apellidos_esperados': 'PEREZ',
    'dni_esperado': '12345678'
}
res = requests.post("http://localhost:8000/api/kyc/dni-single", files=files, data=data)
print(res.status_code)
print(res.text)
